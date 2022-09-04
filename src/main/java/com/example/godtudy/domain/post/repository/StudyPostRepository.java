package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.post.entity.StudyPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long>, CustomStudyPostRepository {

    //    Post + MEMBER 조회 -> 쿼리 1번 발생
    @EntityGraph(attributePaths = {"member"})
    Optional<StudyPost> findMemberById(Long postId);

    Optional<StudyPost> findByTitle(String title);
}
