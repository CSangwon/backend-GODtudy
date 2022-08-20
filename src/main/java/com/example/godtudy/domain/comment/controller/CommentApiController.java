package com.example.godtudy.domain.comment.controller;

import com.example.godtudy.domain.comment.dto.request.CommentUpdateDto;
import com.example.godtudy.global.dto.ResultResponse;
import com.example.godtudy.domain.comment.dto.request.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.response.CommentResponseDto;
import com.example.godtudy.domain.comment.service.CommentService;
import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment/")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("{postType}/{postId}/new")
    public ResponseEntity<ResultResponse<CommentResponseDto>> createComment(@PathVariable String postType, @PathVariable Long postId,
                                                                            @CurrentMember Member member,
                                                                            @RequestBody CommentSaveDto commentSaveDto) {
        ResultResponse<CommentResponseDto> result = ResultResponse.<CommentResponseDto>builder()
                .response(commentService.saveComment(postType, postId, member, commentSaveDto))
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 대댓글 생성
    @PostMapping("{postType}/{postId}/{parentId}/new")
    public ResponseEntity<ResultResponse<CommentResponseDto>> createReComment(@PathVariable String postType, @PathVariable Long postId,
                                                                              @PathVariable Long parentId, @CurrentMember Member member,
                                                                              @RequestBody CommentSaveDto commentSaveDto) {
        ResultResponse<CommentResponseDto> result = ResultResponse.<CommentResponseDto>builder()
                .response(commentService.saveReComment(postType, postId, member, parentId, commentSaveDto))
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 댓글 수정
    @PostMapping("{postType}/{postId}/{commentId}")
    public ResponseEntity<ResultResponse<CommentResponseDto>> updateComment(@PathVariable String postType, @PathVariable Long postId,
                                                                            @PathVariable Long commentId, @CurrentMember Member member,
                                                                            @RequestBody CommentUpdateDto commentUpdateDto) {
        ResultResponse<CommentResponseDto> result = ResultResponse.<CommentResponseDto>builder()
                .response(commentService.updateComment(postType, commentId, member, commentUpdateDto))
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 댓글 삭제

    @DeleteMapping("{postType}")
    public ResponseEntity<?> deleteComment(@PathVariable String postType, @RequestHeader("Post-Id") Long postId,
                                           @RequestHeader("Comment-Id") Long commentId, @CurrentMember Member member){
        commentService.deleteComment(postType, commentId, member);
        return new ResponseEntity<>("Delete Complete", HttpStatus.OK);

    }
}