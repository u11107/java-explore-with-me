package ru.practicum.explore.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.EventState;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.event.service.EventService;
import ru.practicum.explore.exception.ForbiddenRequestException;
import ru.practicum.explore.exception.InputDataException;
import ru.practicum.explore.exception.ObjectNotFoundException;
import ru.practicum.explore.request.dto.ParticipationRequestDto;
import ru.practicum.explore.request.mapper.ParticipationRequestMapper;
import ru.practicum.explore.request.model.EventRequestState;
import ru.practicum.explore.request.model.ParticipationRequest;
import ru.practicum.explore.request.repository.ParticipationRequestRepository;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;
import ru.practicum.explore.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper requestMapper;
    private final UserService userService;
    private final EventService eventService;

    public List<ParticipationRequestDto> getRequestsByUserId(long userId) {
        if (!userService.checkUserId(userId)) {
            throw new InputDataException("User not found");
        }
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto addRequestByUserId(long userId, long eventId) throws ObjectNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User not found id=" + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found id=" + userId));
        if (Objects.equals(eventRepository.findById(eventId).get().getInitiator().getId(), userId)) {
            throw new ForbiddenRequestException("Event initiator is wrong");
        }
        if (eventRepository.findById(eventId).get().getState() == EventState.PUBLISHED) {
            ParticipationRequest participationRequest = ParticipationRequest.builder()
                    .created(LocalDateTime.now())
                    .event(event)
                    .requester(user)
                    .status(EventRequestState.PENDING)
                    .build();
            Long count = requestRepository.countByEvent_IdAndStatus(eventId,
                    EventRequestState.CONFIRMED);
            if (!event.isRequestModeration()) {
                participationRequest.setStatus(EventRequestState.CONFIRMED);
            }
            if (event.getParticipantLimit() != 0 && Objects.equals(event.getParticipantLimit(), count)) {
                participationRequest.setStatus(EventRequestState.REJECTED);
            }
            return requestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
        } else {
            throw new ForbiddenRequestException("Event has state not published");
        }
    }

    public ParticipationRequestDto cancelRequestByUserId(long userId, long requestId) throws ObjectNotFoundException {
        ParticipationRequest req = requestRepository.findById(requestId).orElseThrow(
                () -> new ObjectNotFoundException("Request not found id=" + requestId));
        if (req.getRequester().getId() == userId) {
            req.setStatus(EventRequestState.CANCELED);
            return requestMapper.toParticipationRequestDto(requestRepository.save(req));
        } else {
            throw new InputDataException("User is not create request");
        }
    }

    public List<ParticipationRequestDto> getInfRequestByUserId(long userId, long eventId) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        validateInitiatorForEvent(userId, event);
        return requestRepository.findAllByEvent(eventId, userId)
                .stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        ParticipationRequest participationRequest = getRequestById(reqId);
        validateInitiatorForEvent(userId, event);

        Long limitParticipant = requestRepository.countByEvent_IdAndStatus(eventId,
                EventRequestState.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == limitParticipant) {
            participationRequest.setStatus(EventRequestState.REJECTED);
        }
        participationRequest.setStatus(EventRequestState.CONFIRMED);

        return requestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        ParticipationRequest request = getRequestById(reqId);
        if (eventId != request.getEvent().getId() && userId != request.getRequester().getId()) {
            throw new InputDataException("Incorrect event id or user id");
        }
        request.setStatus(EventRequestState.REJECTED);
        ParticipationRequest requestCanceled = saveRequest(request);
        return requestMapper.toParticipationRequestDto(requestCanceled);
    }


    public ParticipationRequest getRequestById(long reqId) throws ObjectNotFoundException {
        return requestRepository.findById(reqId).orElseThrow(
                () -> new ObjectNotFoundException("Participation request not found"));
    }

    public ParticipationRequest saveRequest(ParticipationRequest request) {
        return requestRepository.save(request);
    }

    private void validateInitiatorForEvent(Long userId, Event event) {
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenRequestException("User is not initiator event");
        }
    }
}
