package ru.practicum.explore.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
