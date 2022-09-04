package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.StudyPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminPostRepository extends JpaRepository<AdminPost, Long>, CustomAdminPostRepository {

    Optional<AdminPost> findByTitle(String title);

    @EntityGraph(attributePaths = {"member"})
    Optional<AdminPost> findAuthorById(Long postId);



}
