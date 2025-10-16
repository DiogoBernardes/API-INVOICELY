package com.api.invoicely.controller;

import com.api.invoicely.dto.category.CategoryCreateDTO;
import com.api.invoicely.dto.category.CategoryResponseDTO;
import com.api.invoicely.dto.category.CategoryUpdateDTO;
import com.api.invoicely.entity.User;
import com.api.invoicely.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable UUID categoryId, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryService.getCategoryById(owner, categoryId));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryService.getAllCategories(owner));
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryCreateDTO dto, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryService.createCategory(owner, dto));
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID categoryId, @RequestBody CategoryUpdateDTO dto, Authentication authentication) {
        User owner = (User) authentication.getPrincipal();
        return ResponseEntity.ok(categoryService.updateCategory(owner, categoryId, dto));
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal User user, @PathVariable UUID categoryId) {
        categoryService.deleteCategory(user, categoryId);
        return ResponseEntity.noContent().build();
    }
}
