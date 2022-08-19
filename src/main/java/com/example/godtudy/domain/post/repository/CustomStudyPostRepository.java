package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.StudyPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStudyPostRepository {

    Page<StudyPost> search(PostSearchCondition postSearchCondition, Pageable pageable);
}
