package com.example.godtudy.domain.member.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProfilePagingDto {

    private int totalPageCount;

    private int currentPageNumber;

    private Long totalElementCount;

    private int currentPageElementCount;

    private List<ProfileResponseDto> profileDtoList = new ArrayList<>();

    public ProfilePagingDto(Page<ProfileResponseDto> profileDtoPage) {
        this.totalPageCount = profileDtoPage.getTotalPages();
        this.currentPageNumber = profileDtoPage.getNumber();
        this.totalElementCount = profileDtoPage.getTotalElements();
        this.currentPageElementCount = profileDtoPage.getNumberOfElements();
        this.profileDtoList = profileDtoPage.getContent();
    }
}
