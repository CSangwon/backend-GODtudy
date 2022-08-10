package com.example.godtudy.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateTodoRequestDto {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime endDate;
}
