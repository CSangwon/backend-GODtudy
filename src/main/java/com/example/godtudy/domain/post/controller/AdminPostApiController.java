package com.example.godtudy.domain.post.controller;

import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.service.AdminPostService;
import com.example.godtudy.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPostApiController {

    @Qualifier("adminPostService")
    private final PostService postService;


    @PostMapping("{post}/new")
    public ResponseEntity<?> createNoticeEvent(@PathVariable String post ,@CurrentMember Member member,
                                               @RequestBody PostSaveRequestDto postSaveRequestDto,
                                               @RequestPart List<MultipartFile> files) throws IOException {
        if (files.isEmpty()) {
            return postService.createPost(member, post, postSaveRequestDto);
        }
        return postService.createPost(member,files, post, postSaveRequestDto);
    }

    @PostMapping("/{post}/{postId}")
    public ResponseEntity<?> updateNoticeEvent(@PathVariable String post, @PathVariable Long postId, @CurrentMember Member member,
                                               @RequestBody PostUpdateRequestDto postUpdateRequestDto,
                                               @RequestPart List<MultipartFile> files) throws IOException {
        if (files.isEmpty()) {
            return postService.updatePost(member, post, postId , postUpdateRequestDto);
        }
        return postService.updatePost(member, post,files, postId , postUpdateRequestDto);
    }

    @DeleteMapping("/{post}/{postId}")
    public ResponseEntity<?> deleteNoticeEvent(@PathVariable String post, @PathVariable Long postId, @CurrentMember Member member) {
        return postService.deletePost(member, postId);
    }

    @GetMapping("{post}/{postId}")
    public ResponseEntity<?> getPostInfo(@PathVariable String post, @PathVariable Long postId) {
        return new ResponseEntity<>(postService.getPostInfo(postId), HttpStatus.OK);
    }
}
