package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.dto.response.PostPagingDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class StudyPostService implements PostService{


    @Override
    public ResponseEntity<?> createPost(Member member, String post, PostSaveRequestDto postSaveRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> createPost(Member member, List<MultipartFile> files, String post, PostSaveRequestDto postSaveRequestDto) throws IOException {
        return null;
    }

    @Override
    public ResponseEntity<?> updatePost(Member member, String post, Long id, PostUpdateRequestDto postUpdateRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> updatePost(Member member, String post, List<MultipartFile> files, Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException {
        return null;
    }

    @Override
    public ResponseEntity<?> deletePost(Member member, String post, Long id) {
        return null;
    }

    @Override
    public PostInfoResponseDto getPostInfo(Long id) {
        return null;
    }

    @Override
    public PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        return null;
    }
}
