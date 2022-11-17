package ru.practicum.explore.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.comment.service.CommentService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events/{eventId}/comment")
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("/{comId}")
    public void deleteCommentByAdmin(@PathVariable long eventId, @PathVariable long comId) {
        log.info("Deleting comment by admin event id={} comment by id={}", eventId, comId);
        commentService.deleteCommentFromAdminById(eventId, comId);
    }
}
