package com.example.godtudy.domain.comment.dto.request;

import com.example.godtudy.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentSaveDto {

    private String content;

    public Comment toEntity() {
        return Comment.builder().content(content).childComment(new ArrayList<>()).build();
    }
}
