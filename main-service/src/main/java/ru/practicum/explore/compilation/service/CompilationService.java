package ru.practicum.explore.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.compilation.dto.CompilationDto;
import ru.practicum.explore.compilation.dto.NewCompilationDto;
import ru.practicum.explore.compilation.mapper.CompilationMapper;
import ru.practicum.explore.compilation.model.Compilation;
import ru.practicum.explore.compilation.repository.CompilationRepository;
import ru.practicum.explore.compilation.model.CompilationEvent;
import ru.practicum.explore.compilation.repository.CompilationEventRepository;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.service.EventService;
import ru.practicum.explore.exception.InputDataException;
import ru.practicum.explore.exception.ObjectNotFoundException;
import ru.practicum.explore.trait.PageTool;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService implements PageTool {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getAllCompilations(int from, int size, boolean pinned) {
        if (size <= from) {
            throw new InputDataException("Incorrect parameters 'from' or 'size'");
        }
        Pageable page = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinned(pinned, page)
                .stream()
                .map(i -> compilationMapper.toCompilationDto(i, getCompilationEvents(i.getId())))
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = findCompilationById(compId);
        return compilationMapper.toCompilationDto(compilation, getCompilationEvents(compId));
    }

    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = compilationMapper.toCompilation(compilationDto);
        Compilation compilationDb = compilationRepository.save(compilation);
        long compId = compilationDb.getId();
        if (!compilationDto.getEvents().contains(null)) {
            for (long id : compilationDto.getEvents()) {
                CompilationEvent compilationEvent = new CompilationEvent(null, compId, id);
                compilationEventRepository.save(compilationEvent);
            }
        }
        return compilationMapper.toCompilationDto(compilationDb, getCompilationEvents(compId));
    }

    public void addEventInCompilation(long compId, long eventId) {
        CompilationEvent compilationEvent = new CompilationEvent(null, compId, eventId);
        compilationEventRepository.save(compilationEvent);
    }

    public void pinCompilation(long compId) {
        Compilation compilation = findCompilationById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    public void deleteCompilationById(long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public void deleteEventFromCompilation(long compId, long eventId) {
        compilationEventRepository.deleteByCompilationAndEvent(compId, eventId);
    }

    public void unpinCompilation(long compId) {
        Compilation compilation = findCompilationById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    private List<EventShortDto> getCompilationEvents(Long compId) {
        List<Long> ids = compilationEventRepository.findCompilationEventIds(compId);
        return eventService.getEventsByIds(ids);
    }

    private Compilation findCompilationById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Compilation not found"));
    }

    public void checkCompilation(Long id) {
        if (compilationRepository.findById(id).isEmpty()) {
            throw new ObjectNotFoundException("Compilation not found id = " + id);
        }
    }
}
