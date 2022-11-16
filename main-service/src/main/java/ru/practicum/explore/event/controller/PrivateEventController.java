package ru.practicum.explore.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.model.UpdateEventRequest;
import ru.practicum.explore.event.service.EventService;
import ru.practicum.explore.exception.ObjectNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class PrivateEventController {
    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsByUserId(@PathVariable long userId,
                                                    @RequestParam(defaultValue = FROM) int from,
                                                    @RequestParam(defaultValue = SIZE) int size) {
        log.info("Getting all events by user id={}", userId);
        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @PatchMapping
    public EventFullDto changeEventFromUser(@PathVariable long userId, @RequestBody UpdateEventRequest eventRequest)
            throws ObjectNotFoundException {
        log.info("Changing event by user id={}", userId);
        return eventService.changeEventFromUser(userId, eventRequest);
    }

    @PostMapping
    public EventFullDto addEvent(@PathVariable long userId, @Valid @RequestBody NewEventDto eventDto)
            throws ObjectNotFoundException {
        log.info("Creating event");
        return eventService.addEvent(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getInfoAboutEventByUserId(@PathVariable long userId, @PathVariable long eventId)
            throws ObjectNotFoundException {
        log.info("Getting information about event id={}", eventId);
        return eventService.getInfoAboutEventByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEvent(@PathVariable long userId, @PathVariable long eventId)
            throws ObjectNotFoundException {
        log.info("Canceling event id={} by user id={}", eventId, userId);
        return eventService.cancelEvent(userId, eventId);
    }

}
