package ru.practicum.explore.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore.dto.EndpointHit;
import ru.practicum.explore.model.Hit;
import ru.practicum.explore.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HitMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(LocalDateTime.parse(endpointHit.getTimestamp(), FORMATTER))
                .build();
    }

    public EndpointHit toEndpointHit(Hit hit) {
        return EndpointHit.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp().format(FORMATTER))
                .build();
    }

    public ViewStats toViewStats(List<Hit> list) {
        if (list.isEmpty()) {
            return null;
        }
        return ViewStats.builder()
                .app(list.get(0).getApp())
                .uri(list.get(0).getUri())
                .hits(list.size())
                .build();
    }
}
