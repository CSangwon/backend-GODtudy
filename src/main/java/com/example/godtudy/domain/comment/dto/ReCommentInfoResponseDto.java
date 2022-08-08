package com.example.godtudy.domain.comment.dto;

import com.example.godtudy.domain.comment.entity.Comment;
import lombok.Data;

@Data
public class ReCommentInfoResponseDto {

    private final static String DELETED_COMMENT_CONTENT = "삭제된 댓글 입니다.";
    private final static String DELETED_COMMENT_AUTHOR = "익명";

    private Long postId;

    private Long commentId;

    private String content;

    private String author;

    public ReCommentInfoResponseDto(Comment reComment) {
        this.postId = reComment.getAdminPost().getId();
        this.commentId = reComment.getId();
        if (reComment.getWriter() == null && reComment.getContent().equals("")) {
            this.content = DELETED_COMMENT_CONTENT;
            this.author = DELETED_COMMENT_AUTHOR;
        } else{
            this.content = reComment.getContent();
            this.author = reComment.getWriter().getNickname();
        }

    }

}
