package ru.practicum.explore.event.mapper;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.explore.category.mapper.CategoryMapper;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.client.stats.StatsClient;
import ru.practicum.explore.comment.mapper.CommentMapper;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EventMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final StatsClient statsClient;
    private final CommentMapper commentMapper;

    public Event toEvent(NewEventDto eventDto, LocalDateTime eventDate) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(new Category(eventDto.getCategory(), null))
                .description(eventDto.getDescription())
                .eventDate(eventDate)
                .location(eventDto.getLocation())
                .paid(eventDto.isPaid())
                .cratedOn(LocalDateTime.now())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .title(eventDto.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().format(FORMATTER))
                .createdOn(event.getCratedOn().format(FORMATTER))
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(FORMATTER) : null)
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(getViews(event.getId()))
                .confirmedRequests(event.getConfirmedRequests())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .comments(event.getComments() != null
                        ? event.getComments().stream().map(commentMapper::toCommentDto).collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(getViews(event.getId()))
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    private Integer getViews(Long eventId) {
        String start = LocalDateTime.of(2022, 10, 1, 0, 0, 0).format(FORMATTER);
        String end = LocalDateTime.now().format(FORMATTER);
        ResponseEntity<Object> responseEntity = statsClient.getStats(start, end, List.of("/events/" + eventId),
                                                                                                        false);
        List<LinkedHashMap> result = (List<LinkedHashMap>) responseEntity.getBody();
        return result.isEmpty() ? 0 : (Integer) result.get(0).get("hits");
    }
}
