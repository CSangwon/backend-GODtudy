package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.entity.AdminPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomAdminPostRepository {

    Page<AdminPost> search(PostSearchCondition postSearchCondition, Pageable pageable);
}
