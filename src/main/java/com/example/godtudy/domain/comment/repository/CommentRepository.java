package com.example.godtudy.domain.comment.repository;

import com.example.godtudy.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByContent(String content);
}