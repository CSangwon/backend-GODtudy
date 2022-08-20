package com.example.godtudy.domain.post.dto.response;

import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.StudyPost;
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

    public BriefPostInfoDto(StudyPost studyPost) {
        this.postId = studyPost.getId();
        this.title = studyPost.getTitle();
        this.content = studyPost.getContent();
        this.author = studyPost.getMember().getNickname();
        this.createdDate = studyPost.getCreatedDate().toString();
    }
}
