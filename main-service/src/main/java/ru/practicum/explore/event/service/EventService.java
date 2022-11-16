package ru.practicum.explore.event.service;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.category.service.CategoryService;
import ru.practicum.explore.client.EndpointHit;
import ru.practicum.explore.client.stats.StatsClient;
import ru.practicum.explore.event.dto.AdminUpdateEventRequest;
import ru.practicum.explore.event.mapper.EventMapper;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.EventSort;
import ru.practicum.explore.event.model.EventState;
import ru.practicum.explore.event.model.UpdateEventRequest;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.event.repository.LocationRepository;
import ru.practicum.explore.exception.InputDataException;
import ru.practicum.explore.exception.ObjectNotFoundException;
import ru.practicum.explore.trait.PageTool;
import ru.practicum.explore.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements PageTool {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private final StatsClient statsClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveHitInStatsService(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        statsClient.save(endpointHit);
    }

    public Event getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Event not found id=" + id));
    }

    public List<EventFullDto> findEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (from < 0 && size < from) {
            throw new InputDataException("Incorrect parameters 'from' of 'size'");
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return eventRepository.searchEvents(users, states, categories, rangeStart, rangeEnd, pageable)
                .stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto editEventByAdmin(long eventId, AdminUpdateEventRequest eventRequest)
                                            throws ObjectNotFoundException {
        Event eventDb = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found id=" + eventId));
        Optional.ofNullable(eventRequest.getAnnotation()).ifPresent(eventDb::setAnnotation);
        if (Optional.ofNullable(eventRequest.getCategoryId()).isPresent()) {
            CategoryDto category = categoryService.getCategoryById(eventRequest.getCategoryId());
            Optional.ofNullable(categoryMapper.toCategoryFromCategoryDto(category)).ifPresent(eventDb::setCategory);
        }
        Optional.ofNullable(eventRequest.getDescription()).ifPresent(eventDb::setDescription);
        Optional.ofNullable(eventRequest.getEventDate()).ifPresent(eventDb::setEventDate);
        Optional.ofNullable(eventRequest.getLocation()).ifPresent(eventDb::setLocation);
        Optional.ofNullable(eventRequest.getPaid()).ifPresent(eventDb::setPaid);
        Optional.ofNullable(eventRequest.getParticipantLimit()).ifPresent(eventDb::setParticipantLimit);
        Optional.ofNullable(eventRequest.getRequestModeration()).ifPresent(eventDb::setRequestModeration);
        Optional.ofNullable(eventRequest.getTitle()).ifPresent(eventDb::setTitle);

        return eventMapper.toEventFullDto(eventRepository.save(eventDb));
    }

    public EventFullDto publishEventByAdmin(Long eventId) {
        Event eventDb = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found id=" + eventId));
        if (eventDb.getState() == EventState.PENDING) {
            eventDb.setState(EventState.PUBLISHED);
            eventDb.setPublishedOn(LocalDateTime.now());
            return eventMapper.toEventFullDto(eventRepository.save(eventDb));
        } else {
            throw new InputDataException("Parameters event date or state incorrect");
        }
    }

    public EventFullDto rejectEventByAdmin(long eventId) {
        Event eventDb = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found id=" + eventId));
        if (eventDb.getState() != EventState.PUBLISHED) {
            eventDb.setState(EventState.CANCELED);
            return eventMapper.toEventFullDto(eventRepository.save(eventDb));
        } else {
            throw new InputDataException("Event cannot be canceled");
        }
    }

    public List<EventShortDto> getAllEventsByUserId(String text, List<Long> categories, boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    boolean onlyAvailable, String sort, int from, int size) {
        if (from < 0 && size < from) {
            throw new InputDataException("Incorrect parameters from of size");
        }
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        List<Event> events = eventRepository.findEvents(text, categories, paid, rangeStart, rangeEnd);
        if (onlyAvailable) {
            events = events
                    .stream()
                    .filter(e -> e.getConfirmedRequests() < e.getParticipantLimit() || e.getParticipantLimit() == 0)
                    .collect(Collectors.toList());
        }
        checkSortEvent(sort);
        EventSort eventSort = EventSort.valueOf(sort);
        switch (eventSort) {
            case EVENT_DATE:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
                break;
            case VIEWS:
                events = events
                        .stream()
                        .sorted(Comparator.comparingLong(Event::getViews))
                        .collect(Collectors.toList());
                break;
            }

        return events
                .stream()
                .skip(from)
                .limit(size)
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }



    public EventFullDto getEventByIdAndStatePublished(long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(
                () -> new ObjectNotFoundException("Event not found id=" + id));
        return eventMapper.toEventFullDto(event);
    }

    public List<EventShortDto> getAllEventsByUserId(long userId, int from, int size) {
        if (!userService.checkUserId(userId)) {
            throw new InputDataException("User with id = " + userId + " not found");
        }

        Pageable page = PageRequest.of(from / size, size);
        Collection<Event> result;
        result = eventRepository.findAllByInitiatorId(userId, page);
        return result
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public EventFullDto addEvent(long userId, NewEventDto eventDto) {
        if (!userService.checkUserId(userId)) {
            throw new InputDataException("User with id = " + userId + " not found");
        }
        if (!LocalDateTime.parse(eventDto.getEventDate(), FORMATTER).isAfter(LocalDateTime.now().minusHours(2))) {
            throw new InputDataException("Event must be no later than 2 hours before the current time. Event date "
                                                                                            + eventDto.getEventDate());
        }
        LocalDateTime eventDate = LocalDateTime.parse(eventDto.getEventDate(), FORMATTER);
        Event event = eventMapper.toEvent(eventDto, eventDate);
        event.setInitiator(userService.getUserById(userId));
        event.setState(EventState.PENDING);
        locationRepository.save(eventDto.getLocation());
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public EventFullDto changeEventFromUser(long userId, UpdateEventRequest eventRequest) {
        checkUpdateEventRequest(userId, eventRequest);
        Event eventDb = eventRepository.findById(eventRequest.getEventId()).get();
        if (eventDb.getState() == EventState.PUBLISHED) {
            throw new InputDataException("Event status does not allow editing");
        }
        Optional.ofNullable(eventRequest.getAnnotation()).ifPresent(eventDb::setAnnotation);
        Category category = categoryMapper.toCategoryFromCategoryDto(
                                                        categoryService.getCategoryById(eventRequest.getCategory()));
        Optional.ofNullable(category).ifPresent(eventDb::setCategory);
        Optional.ofNullable(eventRequest.getDescription()).ifPresent(eventDb::setDescription);
        Optional.of(LocalDateTime.parse(eventRequest.getEventDate(), FORMATTER)).ifPresent(eventDb::setEventDate);
        Optional.ofNullable(eventRequest.getPaid()).ifPresent(eventDb::setPaid);
        Optional.ofNullable(eventRequest.getParticipantLimit()).ifPresent(eventDb::setParticipantLimit);
        Optional.ofNullable(eventRequest.getTitle()).ifPresent(eventDb::setTitle);
        eventDb.setState(EventState.PENDING);
        return eventMapper.toEventFullDto(eventRepository.save(eventDb));
    }

    public EventFullDto getInfoAboutEventByUserId(long userId, long eventId) {
        Event eventDb = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found"));
        if (userId != eventDb.getInitiator().getId()) {
            throw new InputDataException("User with id = " + userId + " not create event with id = " + eventId);
        } else {
            return eventMapper.toEventFullDto(eventDb);
        }
    }

    public EventFullDto cancelEvent(long userId, long eventId) {
        Event eventDb = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found"));
        if (userId != eventDb.getInitiator().getId()) {
            throw new InputDataException("User with id = " + userId + " not create event with id = " + eventId);
        }
        if (eventDb.getState() == EventState.PENDING) {
            eventDb.setState(EventState.CANCELED);
            return eventMapper.toEventFullDto(eventRepository.save(eventDb));
        } else {
            throw new InputDataException("Event cannot be cancelled.");
        }
    }

    public void checkUpdateEventRequest(long userId, UpdateEventRequest eventRequest) throws ObjectNotFoundException {
        if (!userService.checkUserId(userId)) {
            throw new InputDataException("User with id = " + userId + " not found");
        }
        if (LocalDateTime.parse(eventRequest.getEventDate(), FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InputDataException("Incorrect event date");
        }

        eventRepository.findById(eventRequest.getEventId()).orElseThrow(
                () -> new ObjectNotFoundException("Event not found"));
        if (!categoryService.checkCategoryId(eventRequest.getCategory())) {
            throw new InputDataException("Incorrect category id = " + eventRequest.getCategory());
        }
    }

    public List<EventShortDto> getEventsByIds(List<Long> ids) {
        return eventRepository.findAllById(ids)
                .stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    public void checkSortEvent(String result) {
        boolean flag = false;

        for (EventSort sort : EventSort.values()) {
            if (sort.name().equals(result)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new InputDataException("Unknown state: " + result);
        }
    }
}
