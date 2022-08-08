package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    ResponseEntity<?> createPost(Member member, String post, PostSaveRequestDto postSaveRequestDto);

    ResponseEntity<?> createPost(Member member, List<MultipartFile> files, String post,
                                      PostSaveRequestDto postSaveRequestDto) throws IOException;

    ResponseEntity<?> updatePost(Member member, String post, Long id, PostUpdateRequestDto postUpdateRequestDto);

    ResponseEntity<?> updatePost(Member member, String post, List<MultipartFile> files,
                                      Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException;

    ResponseEntity<?> deletePost(Member member, Long id);

    PostInfoResponseDto getPostInfo(Long postId);

}
