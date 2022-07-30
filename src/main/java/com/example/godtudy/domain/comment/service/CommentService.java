package com.example.godtudy.domain.comment.service;

import com.example.godtudy.domain.comment.dto.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.CommentUpdateDto;
import com.example.godtudy.domain.member.entity.Member;
import org.springframework.http.ResponseEntity;

public interface CommentService {

    ResponseEntity<?> saveComment(Long postId, Member member, CommentSaveDto commentSaveDto);

    ResponseEntity<?> saveReComment(Long postId, Member member, Long firstCommentId, CommentSaveDto commentSaveDto);

    ResponseEntity<?> update(Long id,  Member member, CommentUpdateDto commentUpdateDto);

    void delete(Long id,  Member member);


}
