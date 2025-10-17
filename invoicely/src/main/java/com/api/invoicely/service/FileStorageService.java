package com.api.invoicely.service;

import com.api.invoicely.config.R2Properties;
import com.api.invoicely.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final R2Properties r2Properties;

    private S3Client getS3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(r2Properties.getEndpoint()))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(r2Properties.getAccessKey(), r2Properties.getSecretKey())))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }

    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;

        String filename = file.getOriginalFilename();
        String key = r2Properties.getEnvironmentFolder() + "/" + folder + "/" + filename;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            getS3Client().putObject(request, RequestBody.fromBytes(file.getBytes()));

        } catch (IOException e) {
            throw new ApiException("Erro ao fazer upload do arquivo", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        String readableFilename = "budget_" + file.getOriginalFilename();
        return r2Properties.getEndpoint() + "/" + r2Properties.getBucket() + "/" + key + "?filename=" + readableFilename;
    }
}