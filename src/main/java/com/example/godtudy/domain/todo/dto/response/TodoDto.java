package com.example.godtudy.domain.todo.dto.response;

import com.example.godtudy.domain.todo.entity.Todo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoDto {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime endDate;

    private String studyUrl;

    public TodoDto(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.content = todo.getContent();
        this.endDate = todo.getEndDate();
        this.studyUrl = todo.getStudy().getUrl();
    }
}
