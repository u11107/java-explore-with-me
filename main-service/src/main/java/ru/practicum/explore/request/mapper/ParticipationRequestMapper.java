package ru.practicum.explore.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore.request.dto.ParticipationRequestDto;
import ru.practicum.explore.request.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

@Component
public class ParticipationRequestMapper {
    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
