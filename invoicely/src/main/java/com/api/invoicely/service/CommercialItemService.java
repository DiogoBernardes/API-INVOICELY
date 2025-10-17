package com.api.invoicely.service;

import com.api.invoicely.dto.comercialItem.CommercialItemCreateDTO;
import com.api.invoicely.dto.comercialItem.CommercialItemResponseDTO;
import com.api.invoicely.dto.comercialItem.CommercialItemUpdateDTO;
import com.api.invoicely.entity.CommercialItem;
import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.CommercialItemMapper;
import com.api.invoicely.repository.CommercialItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.api.invoicely.mapper.CommercialItemMapper.toComercialItemDTO;


@Service
@RequiredArgsConstructor
public class CommercialItemService {

    private final CommercialItemRepository commercialItemRepository;

    public CommercialItemResponseDTO createCommercialItem(User owner, CommercialItemCreateDTO dto) {
        Company company = owner.getCompany();

        CommercialItem commercialItem = CommercialItem.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .type(dto.getType())
                .company(company)
                .build();

        commercialItemRepository.save(commercialItem);
        return toComercialItemDTO(commercialItem);
    }

    public CommercialItemResponseDTO updateCommercialItem(User owner, UUID commercialItemId, CommercialItemUpdateDTO dto) {
        CommercialItem commercialItem = commercialItemRepository.findActiveByIdAndCompanyId(commercialItemId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Item não encontrado", HttpStatus.NOT_FOUND));

        if (!commercialItem.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para alterar este item", HttpStatus.FORBIDDEN);
        }

        if (dto.getName() != null) commercialItem.setName(dto.getName());
        if (dto.getDescription() != null) commercialItem.setDescription(dto.getDescription());
        if (dto.getPrice() != null) commercialItem.setPrice(dto.getPrice());
        if (dto.getType() != null) commercialItem.setType(dto.getType());

        commercialItemRepository.save(commercialItem);
        return toComercialItemDTO(commercialItem);
    }

    public CommercialItemResponseDTO getCommercialItemById(User owner, UUID commercialItemId) {
        CommercialItem commercialItem = commercialItemRepository.findActiveByIdAndCompanyId(commercialItemId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Item não encontrado", HttpStatus.NOT_FOUND));

        if (!commercialItem.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para ver este item", HttpStatus.FORBIDDEN);
        }

        return toComercialItemDTO(commercialItem);
    }


    public List<CommercialItemResponseDTO> getAllCommercialItems(User owner, String type) {
        List<CommercialItem> commercialItems;

        if (type == null || type.isBlank()) {
            commercialItems = commercialItemRepository.findActiveByCompany(owner.getCompany());
        } else {
            try {
                CommercialItem.ItemType itemType =  CommercialItem.ItemType.valueOf(type);
                commercialItems = commercialItemRepository.findActiveByCompanyAndType(owner.getCompany(), itemType);
            } catch (IllegalArgumentException e) {
                throw new ApiException("Tipo de item inválido: " + type, HttpStatus.BAD_REQUEST);
            }
        }

        return commercialItems.stream()
                .map(CommercialItemMapper::toComercialItemDTO)
                .collect(Collectors.toList());
    }

    public void deleteCommercialItem(User owner, UUID commercialItemId) {
        CommercialItem commercialItem = commercialItemRepository.findActiveByIdAndCompanyId(commercialItemId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Item não encontrado", HttpStatus.NOT_FOUND));

        if (!commercialItem.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para remover este item", HttpStatus.FORBIDDEN);
        }

        commercialItem.setRemovedAt(LocalDateTime.now());
        commercialItemRepository.save(commercialItem);
    }
}
