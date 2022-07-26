package com.example.godtudy.domain.post.dto.response;

import com.example.godtudy.domain.post.entity.AdminPost;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BriefPostInfoDto {

    private Long postId;

    private String title;

    private String content;

    private String author;

    private String createdDate;

    public BriefPostInfoDto(AdminPost adminPost) {
        this.postId = adminPost.getId();
        this.title = adminPost.getTitle();
        this.content = adminPost.getContent();
        this.author = adminPost.getMember().getNickname();
        this.createdDate = adminPost.getCreatedDate().toString();
    }
}
