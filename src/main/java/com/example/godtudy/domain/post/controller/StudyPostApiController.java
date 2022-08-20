package com.example.godtudy.domain.post.controller;

import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.dto.response.PostPagingDto;
import com.example.godtudy.domain.post.dto.response.PostResponseDto;
import com.example.godtudy.domain.post.service.StudyPostService;
import com.example.godtudy.global.dto.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyPostApiController {

    private final StudyPostService postService;

    @PostMapping("{studyUrl}/post/{post}")
    public ResponseEntity<ResultResponse<PostResponseDto>> createStudyPost(@CurrentMember Member member,
                                                                           @PathVariable String studyUrl, @PathVariable String post,
                                                                           @RequestPart(required = false) List<MultipartFile> files,
                                                                           @RequestPart PostSaveRequestDto postSaveRequestDto) throws IOException {
        PostResponseDto createPost;
        if (files == null) createPost = postService.createPost(member, post, studyUrl, postSaveRequestDto);
        else createPost = postService.createPost(member, post, studyUrl, files, postSaveRequestDto);


        ResultResponse<PostResponseDto> result = ResultResponse.<PostResponseDto>builder()
                .response(createPost)
                .status(HttpStatus.OK).build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("{studyUrl}/post/{post}/{postId}")
    public ResponseEntity<ResultResponse<PostResponseDto>> updateStudyPost(@CurrentMember Member member, @PathVariable String studyUrl,
                                                                           @PathVariable String post, @PathVariable Long postId,
                                                                           @RequestPart(required = false) List<MultipartFile> files,
                                                                           @RequestPart PostUpdateRequestDto postUpdateRequestDto) throws IOException {
        PostResponseDto updatePost;
        if (files == null) updatePost = postService.updatePost(member, post, studyUrl, postId, postUpdateRequestDto);
        else updatePost = postService.updatePost(member, post, studyUrl, files, postId, postUpdateRequestDto);

        ResultResponse<PostResponseDto> result = ResultResponse.<PostResponseDto>builder()
                .response(updatePost)
                .status(HttpStatus.OK).build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("{studyUrl}/post/{post}")
    public ResponseEntity<?> deleteStudyPost(@CurrentMember Member member, @PathVariable String studyUrl,
                                             @PathVariable String post, @RequestHeader("Post-Id") Long postId) {
        return new ResponseEntity<>(postService.deletePost(member, post, studyUrl, postId), HttpStatus.OK);
    }

    @GetMapping("{studyUrl}/post/{post}/{postId}")
    public ResponseEntity<ResultResponse<PostInfoResponseDto>> getStudyPostInfo(@PathVariable String studyUrl,
                                                                                @PathVariable String post,
                                                                                @PathVariable Long postId) {
        ResultResponse<PostInfoResponseDto> result = ResultResponse.<PostInfoResponseDto>builder()
                .response(postService.getPostInfo(postId))
                .status(HttpStatus.OK).build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("{studyUrl}/post/{post}")
    public ResponseEntity<ResultResponse<PostPagingDto>> getAdminPostList(@PathVariable String studyUrl, @PathVariable String post,
                                                                          @RequestBody PostSearchCondition postSearchCondition,
                                                                          @PageableDefault(size = 12, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        ResultResponse<PostPagingDto> result = ResultResponse.<PostPagingDto>builder()
                .response(postService.getPostList(pageable, postSearchCondition))
                .status(HttpStatus.OK).build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}