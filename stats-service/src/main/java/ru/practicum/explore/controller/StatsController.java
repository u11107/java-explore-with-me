package ru.practicum.explore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.dto.EndpointHit;
import ru.practicum.explore.dto.ViewStats;
import ru.practicum.explore.service.StatsService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHit createHit(@RequestBody EndpointHit endpointHit) {
        log.info("Create hit {}", endpointHit);
        return statsService.createHit(endpointHit);
    }

    @GetMapping("/stats")
    public Collection<ViewStats> getStats(@RequestParam String start,
                                          @RequestParam String end,
                                          @RequestParam List<String> uris,
                                          @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Get stats {}", uris);
        return statsService.getStats(start, end, uris, unique);
    }
}
