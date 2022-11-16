package ru.practicum.explore.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.NewCompilationDto;
import ru.practicum.explore.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Adding compilation");
        return compilationService.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> addEventInCompilation(@PathVariable long compId, @PathVariable long eventId) {
        log.info("Adding event in compilation");
        compilationService.addEventInCompilation(compId, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> pinCompilation(@PathVariable long compId) {
        log.info("Pining compilation");
        compilationService.pinCompilation(compId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilationById(@PathVariable long compId) {
        log.info("Deleting compilation");
        compilationService.deleteCompilationById(compId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public ResponseEntity<HttpStatus> deleteEventFromCompilation(@PathVariable long compId, @PathVariable long eventId) {
        log.info("Deleting event compilation");
        compilationService.deleteEventFromCompilation(compId, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{compId}/pin")
    public ResponseEntity<HttpStatus> unpinCompilation(@PathVariable long compId) {
        log.info("Unpinning compilation");
        compilationService.unpinCompilation(compId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
