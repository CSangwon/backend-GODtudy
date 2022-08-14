package com.example.godtudy.domain.comment.service;

import com.example.godtudy.domain.comment.dto.request.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.request.CommentUpdateDto;
import com.example.godtudy.domain.comment.dto.response.CommentResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;

public interface CommentService {

    CommentResponseDto saveComment(String postType, Long postId, Member member, CommentSaveDto commentSaveDto);

    CommentResponseDto saveReComment(String postType, Long postId, Member member, Long firstCommentId, CommentSaveDto commentSaveDto);

    ResponseEntity<?> updateComment(Long id,  Member member, CommentUpdateDto commentUpdateDto);

    void deleteComment(String postType, Long id,  Member member);


}
