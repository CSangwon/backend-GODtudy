package com.example.godtudy.domain.todo.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTodoRequestDto {

    private String title;

    private String content;

    private LocalDateTime endDate;

}
