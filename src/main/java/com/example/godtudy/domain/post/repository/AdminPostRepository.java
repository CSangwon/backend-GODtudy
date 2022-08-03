package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.post.entity.AdminPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminPostRepository extends JpaRepository<AdminPost, Long> {

    Optional<AdminPost> findByTitle(String title);

    @EntityGraph(attributePaths = {"member"})
    Optional<AdminPost> findAuthorById(Long postId);
}
