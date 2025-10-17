package com.api.invoicely.service;

import com.api.invoicely.dto.category.CategoryCreateDTO;
import com.api.invoicely.dto.category.CategoryResponseDTO;
import com.api.invoicely.dto.category.CategoryUpdateDTO;
import com.api.invoicely.entity.Category;
import com.api.invoicely.entity.Company;
import com.api.invoicely.entity.User;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.CategoryMapper;
import com.api.invoicely.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static com.api.invoicely.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO createCategory(User owner, CategoryCreateDTO dto) {
        Company company = owner.getCompany();

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .company(company)
                .build();

        categoryRepository.save(category);
        return toCategoryDto(category);
    }

    public CategoryResponseDTO updateCategory(User owner, UUID categoryId, CategoryUpdateDTO dto) {
        Category category = categoryRepository.findActiveByIdAndCompanyId(categoryId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));

        if (!category.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para alterar esta categoria", HttpStatus.FORBIDDEN);
        }

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());


        categoryRepository.save(category);
        return toCategoryDto(category);
    }

    public CategoryResponseDTO getCategoryById(User owner, UUID categoryId) {
        Category category = categoryRepository.findActiveByIdAndCompanyId(categoryId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));

        if (!category.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para ver esta Categoria", HttpStatus.FORBIDDEN);
        }

        return toCategoryDto(category);
    }

    public List<CategoryResponseDTO> getAllCategories(User owner) {
        return categoryRepository.findActiveByCompany(owner.getCompany())
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();

    }

    public void deleteCategory(User owner, UUID categoryId) {
        Category category = categoryRepository.findActiveByIdAndCompanyId(categoryId, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Categoria não encontrada", HttpStatus.NOT_FOUND));

        if (!category.getCompany().getId().equals(owner.getCompany().getId())) {
            throw new ApiException("Não tem permissão para remover esta Categoria", HttpStatus.FORBIDDEN);
        }

        category.setRemovedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
}
