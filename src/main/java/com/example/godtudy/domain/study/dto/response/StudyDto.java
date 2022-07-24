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

    private Teacher teacher;

    private Student student;

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
        this.teacher = new Teacher(study.getTeacher());
        this.student = new Student(study.getStudent());
        this.shortDescription = study.getShortDescription();
    }

    class Student {
        private Long id;
        private String name;
        private String username;

        public Student(Member student) {
            this.id = student.getId();
            this.name = student.getName();
            this.username = student.getUsername();
        }
    }

    class Teacher {
        private Long id;
        private String name;
        private String username;

        public Teacher(Member teacher) {
            this.id = teacher.getId();
            this.name = teacher.getName();
            this.username = teacher.getUsername();
        }
    }
}

