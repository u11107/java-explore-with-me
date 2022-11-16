package ru.practicum.explore.event.dto;

import lombok.*;
import ru.practicum.explore.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    private String annotation;
    @NotNull
    private Long category;
    @NotBlank
    private String description;
    @NotNull
    private String eventDate;
    @NotNull
    private Location location;
    @NotBlank
    private String title;
    private boolean paid;
    private Long participantLimit;
    private boolean requestModeration;
}
