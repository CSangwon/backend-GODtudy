package com.example.godtudy.domain.study.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateStudyRequestDto {

    private String name;

    private String shortDescription;
}
