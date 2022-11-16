package ru.practicum.explore.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.NewCompilationDto;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.event.dto.EventShortDto;

import java.util.List;

@Component
public class CompilationMapper {

    public Compilation toCompilation(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.isPinned())
                .title(compilationDto.getTitle())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> compilationEvents) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(compilationEvents)
                .build();
    }
}
