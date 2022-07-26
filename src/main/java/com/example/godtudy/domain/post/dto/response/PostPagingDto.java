package com.example.godtudy.domain.post.dto.response;


import com.example.godtudy.domain.post.entity.AdminPost;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostPagingDto {
//
//    private int totalPageCount;
//
//    private int currentPageNum;
//
//    private long totalElementCount;
//
//    private int currentPageElementCount;
//
//    private List<BriefPostInfoDto> simplePostDtoList = new ArrayList<>();
//
//    public PostPagingDto(Page<AdminPost> searchResults) {
//        this.totalPageCount = searchResults.getTotalPages();
//        this.currentPageNum = searchResults.getNumber();
//        this.totalElementCount = searchResults.getTotalElements();
//        this.currentPageElementCount = searchResults.getNumberOfElements();
//        this.simplePostDtoList = searchResults.getContent().stream().map(BriefPostInfo::new).toList();
//    }
}
