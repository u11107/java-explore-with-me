package ru.practicum.explore.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.dto.NewCategoryDto;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.category.repository.CategoryRepository;
import ru.practicum.explore.exception.InputDataException;
import ru.practicum.explore.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories(int from, int size) {
        if (from != 0 || size <= from) {
            throw new InputDataException("Incorrect input data");
        }

        Pageable page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new ObjectNotFoundException("Category by id not found"));
        return categoryMapper.toCategoryDto(category);
    }

    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        Category category = categoryMapper.toCategoryFromNewCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public CategoryDto changeCategory(NewCategoryDto categoryDto) {
        if (categoryDto.getId() == 0) {
            throw new InputDataException("Incorrect id");
        }
        Category category = categoryMapper.toCategoryFromNewCategory(categoryDto);
        Category categoryDb = categoryRepository.findById(categoryDto.getId()).orElseThrow(
                () -> new InputDataException("Category by id not found"));
        Optional.ofNullable(category.getName()).ifPresent(categoryDb::setName);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryDb));
    }

    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }

    public boolean checkCategoryId(long catId) {
        return categoryRepository.existsById(catId);
    }
}
