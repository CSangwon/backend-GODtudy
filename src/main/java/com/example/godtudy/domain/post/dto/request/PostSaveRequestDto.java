package com.example.godtudy.domain.post.dto.request;

import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.global.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSaveRequestDto {

    @NotBlank(message = "제목은 필수입력사항입니다..", groups = ValidationGroups.NotEmptyGroup.class)
    private String title;

    @NotBlank(message = "내용을 입력해주세요..", groups = ValidationGroups.NotEmptyGroup.class)
    private String content;

    private String file;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public AdminPost toNoticeEntity(){
        return AdminPost.builder()
                .title(title)
                .content(content)
                .file(file)
                .build();
    }
}
