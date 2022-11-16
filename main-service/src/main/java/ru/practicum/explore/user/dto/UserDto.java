package ru.practicum.explore.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotBlank
    @NonNull
    private String name;
    @NotBlank
    @NonNull
    @Email
    private String email;

}
