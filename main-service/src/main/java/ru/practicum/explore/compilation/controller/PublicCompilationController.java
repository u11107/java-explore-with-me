package ru.practicum.explore.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private static final String FROM = "0";
    private static final String SIZE = "10";
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false, defaultValue = FROM) int from,
                                                   @RequestParam(required = false, defaultValue = SIZE) int size,
                                                   @RequestParam(required = false, defaultValue = "false") Boolean pinned) {
        log.info("Get compilations");
        return compilationService.getAllCompilations(from, size, pinned);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable long compId) {
        log.info("Getting compilation by id=" + compId);
        return compilationService.getCompilationById(compId);
    }
}
