package ru.practicum.explore.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private static final String FROM = "0";
    private static final String SIZE = "10";

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = FROM) int from,
                                              @RequestParam(defaultValue = SIZE) int size) {
        log.info("Get categories /categories");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable long catId) {
        log.info("Get category {}", catId);
        return categoryService.getCategoryById(catId);
    }
}
