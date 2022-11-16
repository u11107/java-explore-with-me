package ru.practicum.explore.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventRequest {
    private String annotation;
    private long category;
    private String description;
    private String eventDate;
    @NotNull
    private Long eventId;
    private Boolean paid;
    private Long participantLimit;
    private String title;
}
