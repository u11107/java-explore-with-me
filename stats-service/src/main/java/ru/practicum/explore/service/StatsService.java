package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.dto.EndpointHit;
import ru.practicum.explore.model.Hit;
import ru.practicum.explore.dto.ViewStats;
import ru.practicum.explore.mapper.HitMapper;
import ru.practicum.explore.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatsService {
    private final StatsRepository statsRepository;
    private final HitMapper hitMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndpointHit createHit(EndpointHit endpointHit) {
        Hit hit = hitMapper.toHit(endpointHit);
        return hitMapper.toEndpointHit(statsRepository.save(hit));
    }

    public Collection<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(end, FORMATTER);
        Collection<ViewStats> viewStats = new ArrayList<>();
        if (uris == null) {
            uris = statsRepository.findAllByTime(startTime, endTime).stream().distinct().collect(Collectors.toList());
        }
        List<Hit> hits;
        for (String uri : uris) {
            if (uri.contains("[")) {
                hits = statsRepository.findAllByUri(uri.substring(1, uri.length() - 1), startTime, endTime);
            } else {
                hits = statsRepository.findAllByUri(uri, startTime, endTime);
            }
            if (hits.size() > 0) {
                viewStats.add(hitMapper.toViewStats(hits));
            }
        }
        return viewStats;
    }
}
