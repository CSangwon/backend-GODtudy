package com.example.godtudy.domain.study.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateStudyRequestDto {

    private String name;

    private Long studentId;

    @NotEmpty
    @Length(min=2, max = 20)
    private String url;

    private String subject;

    private String shortDescription;
}
