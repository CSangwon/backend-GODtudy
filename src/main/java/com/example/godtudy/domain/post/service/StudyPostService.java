package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.dto.response.PostPagingDto;
import com.example.godtudy.domain.post.dto.response.PostResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StudyPostService {

    PostResponseDto createPost(Member member, String post, String studyUrl, PostSaveRequestDto postSaveRequestDto);

    PostResponseDto createPost(Member member,  String post, String studyUrl, List<MultipartFile> files,
                                 PostSaveRequestDto postSaveRequestDto) throws IOException;

    PostResponseDto updatePost(Member member, String post, String studyUrl, Long id, PostUpdateRequestDto postUpdateRequestDto);

    PostResponseDto updatePost(Member member, String post, String studyUrl, List<MultipartFile> files,
                                 Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException;

    ResponseEntity<?> deletePost(Member member, String post, String studyUrl, Long id);

    PostInfoResponseDto getPostInfo(Long postId);

    PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition);
}
