package ru.practicum.explore.comment.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewCommentDto {
    private Long id;
    @NotNull
    @NotBlank
    private String text;
    @NotNull
    private String createdDate;
}
