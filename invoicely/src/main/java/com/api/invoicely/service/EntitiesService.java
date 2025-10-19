package com.api.invoicely.service;

import com.api.invoicely.dto.entities.EntitiesCreateDTO;
import com.api.invoicely.dto.entities.EntitiesUpdateDTO;
import com.api.invoicely.dto.entities.EntitiesResponseDTO;
import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.Entities;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.EntitiesMapper;
import com.api.invoicely.repository.CompanyRepository;
import com.api.invoicely.repository.EntitiesRepository;
import com.api.invoicely.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static com.api.invoicely.mapper.EntitiesMapper.toEntitiesDto;

@Service
@RequiredArgsConstructor
public class EntitiesService {

    private final EntitiesRepository entitiesRepository;
    private final CompanyRepository companyRepository;
    public EntitiesResponseDTO createEntity(User owner, EntitiesCreateDTO dto) {
        Company company = owner.getCompany();

        if (!ValidationUtils.isValidPortugueseNif(dto.getNif())) {
            throw new ApiException("O NIF inserido é inválido.", HttpStatus.BAD_REQUEST);
        }

        if (!ValidationUtils.isValidEmail(dto.getEmail())) {
            throw new ApiException("O email inserido é inválido.", HttpStatus.BAD_REQUEST);
        }

        if (entitiesRepository.existsByNif(dto.getNif()) || companyRepository.existsByNif(dto.getNif())) {
            throw new ApiException("Já existe um registo com este NIF.", HttpStatus.BAD_REQUEST);
        }

        if (entitiesRepository.existsByEmail(dto.getEmail()) || companyRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Já existe um registo com este email.", HttpStatus.BAD_REQUEST);
        }

        Entities entity = Entities.builder()
                .company(company)
                .name(dto.getName())
                .nif(dto.getNif())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .type(dto.getType())
                .build();

        entitiesRepository.save(entity);
        return toEntitiesDto(entity);
    }

    public EntitiesResponseDTO updateEntity(User owner, UUID entityId, EntitiesUpdateDTO dto) {
        Entities entity = entitiesRepository.findActiveById(entityId)
                .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));

        if (!entity.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para alterar esta entidade", HttpStatus.FORBIDDEN);
        }

        if (!ValidationUtils.isValidPortugueseNif(dto.getNif())) {
            throw new ApiException("O NIF inserido é inválido.", HttpStatus.BAD_REQUEST);
        }

        if (!ValidationUtils.isValidEmail(dto.getEmail())) {
            throw new ApiException("O email inserido é inválido.", HttpStatus.BAD_REQUEST);
        }

        if (entitiesRepository.existsByNif(dto.getNif()) || companyRepository.existsByNif(dto.getNif())) {
            throw new ApiException("Já existe um registo com este NIF.", HttpStatus.BAD_REQUEST);
        }

        if (entitiesRepository.existsByEmail(dto.getEmail()) || companyRepository.existsByEmail(dto.getEmail())) {
            throw new ApiException("Já existe um registo com este email.", HttpStatus.BAD_REQUEST);
        }

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getNif() != null) entity.setNif(dto.getNif());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());

        entitiesRepository.save(entity);
        return toEntitiesDto(entity);
    }

    public EntitiesResponseDTO getEntityById(User owner, UUID entityId) {
        Entities entity = entitiesRepository.findActiveById(entityId)
                .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));

        if (!entity.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para ver esta entidade", HttpStatus.FORBIDDEN);
        }

        return toEntitiesDto(entity);
    }

    public List<EntitiesResponseDTO> getAllEntities(User owner, String type) {
        List<Entities> entities;

        if (type == null || type.isBlank()) {
            entities = entitiesRepository.findActiveByCompany(owner.getCompany());
        } else {
            try {
                Entities.EntityType entityType = Entities.EntityType.valueOf(type);
                entities = entitiesRepository.findActiveByCompanyAndType(owner.getCompany(), entityType);
            } catch (IllegalArgumentException e) {
                throw new ApiException("Tipo de entidade inválido: " + type, HttpStatus.BAD_REQUEST);
            }
        }

        return entities.stream()
                .map(EntitiesMapper::toEntitiesDto)
                .collect(Collectors.toList());
    }




    public void deleteEntity(User owner, UUID entityId) {
        Entities entity = entitiesRepository.findActiveById(entityId)
                .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));

        if (!entity.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para remover esta entidade", HttpStatus.FORBIDDEN);
        }

        entity.setRemovedAt(LocalDateTime.now());
        entitiesRepository.save(entity);
    }
}
