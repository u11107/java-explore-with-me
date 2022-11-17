package ru.practicum.explore.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.comment.dto.CommentDto;
import ru.practicum.explore.comment.service.CommentService;
import ru.practicum.explore.comment.dto.NewCommentDto;
import ru.practicum.explore.comment.dto.UpdateCommentDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events/{eventId}/comment")
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto addComment(@PathVariable Long userId, @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto commentDto) {
        log.info("Creating comment by user id={} event id={}", userId, eventId);
        return commentService.addComment(userId, eventId, commentDto);
    }

    @GetMapping("/{comId}")
    public CommentDto getCommentById(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long comId) {
        log.info("Getting comment by user id={} event id={} comment by id={}", userId, eventId, comId);
        return commentService.getCommentById(userId, eventId, comId);
    }

    @PatchMapping("/{comId}")
    public CommentDto updateComment(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long comId,
                                    @Valid @RequestBody UpdateCommentDto commentDto) {
        log.info("Updating comment by user id={} event id={} comment by id={}", userId, eventId, comId);
        return commentService.updateComment(userId, eventId, comId, commentDto);
    }

    @DeleteMapping("/{comId}")
    public void deleteCommentById(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long comId) {
        log.info("Deleting comment by user id={} event id={} comment by id={}", userId, eventId, comId);
        commentService.deleteCommentFromUserById(userId, eventId, comId);
    }
}
