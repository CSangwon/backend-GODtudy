package com.example.godtudy.domain.study.dto.response;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.entity.Study;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudyDto {

    private String name;

    @NotEmpty
    @Length(min=2, max = 20)
    private String url;

    private String subject;

    private Member teacher;

    private Member student;

    private String shortDescription;

    public StudyDto(CreateStudyRequestDto request) {
        this.name = request.getName();
        this.url = request.getUrl();
        this.subject = request.getSubject();
        this.shortDescription = request.getShortDescription();
    }

    public StudyDto(Study study) {
        this.name = study.getName();
        this.url = study.getUrl();
        this.subject = study.getSubject();
        this.teacher = study.getTeacher();
        this.student = study.getStudent();
        this.shortDescription = study.getShortDescription();
    }

    public Study toEntity(){
        return Study.builder()
                .name(name)
                .url(url)
                .subject(subject)
                .teacher(teacher)
                .student(student)
                .shortDescription(shortDescription)
                .build();
    }
}
