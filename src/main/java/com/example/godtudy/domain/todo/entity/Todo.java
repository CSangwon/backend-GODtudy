package com.example.godtudy.domain.todo.entity;

import com.example.godtudy.domain.study.entity.Study;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
public class Todo {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    private String title;

    private LocalDateTime endDate;

    private String content;
}
