package com.example.godtudy.domain.study.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateStudyRequestDto {

    private String name;

    private String shortDescription;
}
