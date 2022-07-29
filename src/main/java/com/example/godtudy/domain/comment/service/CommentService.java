package com.example.godtudy.domain.comment.service;

import com.example.godtudy.domain.comment.dto.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.CommentUpdateDto;
import com.example.godtudy.domain.member.entity.Member;

public interface CommentService {

    void save(Long postId, Member member, CommentSaveDto commentSaveDto);

    void saveReComment(Long postId, Member member, Long firstCommentId, CommentSaveDto commentSaveDto);

    void update(Long id,  Member member, CommentUpdateDto commentUpdateDto);

    void delete(Long id,  Member member);


}
