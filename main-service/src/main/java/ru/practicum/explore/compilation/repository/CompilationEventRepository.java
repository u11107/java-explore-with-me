package ru.practicum.explore.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.compilation.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {
    @Query("SELECT ce.compilation FROM CompilationEvent AS ce " +
            "WHERE ce.compilation = :compId")
    List<Long> findCompilationEventIds(Long compId);

    void deleteByCompilationAndEvent(Long compId, Long eventId);
}
