package com.example.godtudy.domain.post.dto.response;


import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.StudyPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPagingDto {

    private int totalPageCount;

    private int currentPageNum;

    private long totalElementCount;

    private int currentPageElementCount;

    private List<BriefPostInfoDto> simplePostDtoList = new ArrayList<>();


    public PostPagingDto postPagingDtoByAdminPost(Page<AdminPost> searchResultAdminPost) {
        this.totalPageCount = searchResultAdminPost.getTotalPages();
        this.currentPageNum = searchResultAdminPost.getNumber();
        this.totalElementCount = searchResultAdminPost.getTotalElements();
        this.currentPageElementCount = searchResultAdminPost.getNumberOfElements();
        this.simplePostDtoList = searchResultAdminPost.getContent().stream().map(BriefPostInfoDto::new).collect(Collectors.toList());
        return this;
    }

    public void postPagingDtoByStudyPost(Page<StudyPost> searchResultStudyPost) {

    }
}
