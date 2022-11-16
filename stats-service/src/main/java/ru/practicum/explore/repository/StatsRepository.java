package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT h.uri FROM Hit h " +
            "WHERE h.timestamp>=?1 " +
            "AND h.timestamp<=?2")
    List<String> findAllByTime(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT h FROM Hit h " +
            "WHERE h.uri=?1 " +
            "AND h.timestamp>=?2 AND h.timestamp<=?3")
    List<Hit> findAllByUri(String uri, LocalDateTime startTime, LocalDateTime endTime);
}
