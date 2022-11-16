package ru.practicum.explore.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.AdminUpdateEventRequest;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.model.EventState;
import ru.practicum.explore.event.service.EventService;
import ru.practicum.explore.exception.ObjectNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private static final String FROM = "0";
    private static final String SIZE = "10";

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                           @RequestParam(required = false) List<EventState> states,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = FROM) int from,
                                           @RequestParam(defaultValue = SIZE) int size) {
        log.info("Searching events by admin");
        return eventService.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public EventFullDto editEventByAdmin(@PathVariable long eventId,
                                         @RequestBody AdminUpdateEventRequest eventRequest)
                                         throws ObjectNotFoundException {
        log.info("Editing event by admin");
        return eventService.editEventByAdmin(eventId, eventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        log.info("Setting state event is publish");
        return eventService.publishEventByAdmin(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) throws ObjectNotFoundException {
        log.info("Setting state event is canceled");
        return eventService.rejectEventByAdmin(eventId);
    }
}
