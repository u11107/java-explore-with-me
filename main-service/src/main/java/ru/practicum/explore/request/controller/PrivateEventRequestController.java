package ru.practicum.explore.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.request.dto.ParticipationRequestDto;
import ru.practicum.explore.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateEventRequestController {

    private final ParticipationRequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getRequestByUserId(@PathVariable long userId) {
        log.info("Getting requests by user id = " + userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping("/users/{userId}/requests")
    public ParticipationRequestDto addRequestByUserId(@PathVariable long userId, @RequestParam Long eventId) {
        log.info("Posting request by user id = " + userId + " event id = " + eventId);
        return requestService.addRequestByUserId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUserId(@PathVariable long userId, @PathVariable long requestId) {
        log.info("Canceling request id = " + requestId + " by user id = " + userId);
        return requestService.cancelRequestByUserId(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getInfRequestByUserId(@PathVariable long userId, @PathVariable long eventId) {
        log.info("Getting information about request by user id={}", eventId);
        return requestService.getInfRequestByUserId(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequest(@PathVariable long userId, @PathVariable long eventId,
                                                  @PathVariable long reqId) {
        log.info("Confirm request id = {} by user id = {}", reqId, userId);
        return requestService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectRequest(@PathVariable long userId, @PathVariable long eventId,
                                                 @PathVariable long reqId) {
        log.info("Reject request id = {} by user id = {}", reqId, userId);
        return requestService.rejectRequest(userId, eventId, reqId);
    }
}
