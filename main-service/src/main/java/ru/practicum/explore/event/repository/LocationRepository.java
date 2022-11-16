package ru.practicum.explore.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
