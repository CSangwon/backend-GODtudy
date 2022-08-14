package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.post.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
}
