package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {

    ResponseEntity<?> createAdminPost(Member member, String post, PostSaveRequestDto postSaveRequestDto);

    ResponseEntity<?> createAdminPost(Member member, List<MultipartFile> files, String post,
                                      PostSaveRequestDto postSaveRequestDto) throws IOException;

    ResponseEntity<?> updateAdminPost(Member member, String post, Long id, PostUpdateRequestDto postUpdateRequestDto);

    ResponseEntity<?> updateAdminPost(Member member, String post, List<MultipartFile> files,
                                      Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException;

    ResponseEntity<?> deleteAdminPost(Member member, Long id);

    ResponseEntity<?> getAdminPostInfo(Long id);

}
