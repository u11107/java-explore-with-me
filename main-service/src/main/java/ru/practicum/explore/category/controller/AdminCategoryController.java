package ru.practicum.explore.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.dto.NewCategoryDto;
import ru.practicum.explore.category.service.CategoryService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(@RequestBody NewCategoryDto categoryDto) {
        log.info("Creating category {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping
    public CategoryDto changeCategory(@RequestBody NewCategoryDto categoryDto) {
        log.info("Changing category {}", categoryDto);
        return categoryService.changeCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable long catId) {
        log.info("Deleting category {}", catId);
        categoryService.deleteCategory(catId);
    }
}
