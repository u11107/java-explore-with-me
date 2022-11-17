package ru.practicum.explore.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.comment.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
