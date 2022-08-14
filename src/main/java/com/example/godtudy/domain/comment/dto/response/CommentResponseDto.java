package com.example.godtudy.domain.comment.dto.response;

import com.example.godtudy.domain.post.entity.PostEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {

    private String content;

    private String username;

    private String postKind;

    private String message;
}
