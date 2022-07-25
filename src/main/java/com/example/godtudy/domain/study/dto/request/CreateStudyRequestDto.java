package com.example.godtudy.domain.study.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateStudyRequestDto {

    private String name;

    private Long teacherId;

    private Long studentId;

    @NotEmpty
    @Length(min=2, max = 20)
    private String url;

    private String subject;

    private String shortDescription;
}
