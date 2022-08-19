package com.example.godtudy.domain.comment.dto.response;

import com.example.godtudy.domain.comment.entity.Comment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentInfoResponseDto {

    private final static String DELETED_COMMENT_CONTENT = "삭제된 댓글 입니다.";
    private final static String DELETED_COMMENT_AUTHOR = "익명";

    private Long postId;

    private Long commentId;

    private String content;

    private String author;

    private List<ReCommentInfoResponseDto> reCommentInfoResponseDtoList;

    public CommentInfoResponseDto(Comment comment, List<Comment> commentList) {
        if (comment.getAdminPost() != null) {
            this.postId = comment.getAdminPost().getId();
        } else if (comment.getStudyPost() != null) {
            this.postId = comment.getStudyPost().getId();
        }

        this.commentId = comment.getId();

        if (comment.getWriter() == null && comment.getContent().equals("")) {
            this.content = DELETED_COMMENT_CONTENT;
            this.author = DELETED_COMMENT_AUTHOR;
        }else{
            this.content = comment.getContent();
            this.author = comment.getWriter().getNickname();
        }

        List<ReCommentInfoResponseDto> reComments = new ArrayList<>();
        for (Comment reComment : commentList) {
            reComments.add(new ReCommentInfoResponseDto(reComment));
        }

        this.reCommentInfoResponseDtoList = reComments;
    }


}
