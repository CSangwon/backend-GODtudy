package com.example.godtudy.domain.todo.dto.response;

import com.example.godtudy.domain.todo.entity.Todo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodoDto {

    private String title;

    private String content;

    private LocalDateTime endDate;

    private String study_url;

    public TodoDto(Todo todo) {
        this.title = todo.getTitle();
        this.content = todo.getContent();
        this.endDate = todo.getEndDate();
        this.study_url = todo.getStudy().getUrl();
    }
}
