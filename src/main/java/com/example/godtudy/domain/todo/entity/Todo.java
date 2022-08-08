package com.example.godtudy.domain.todo.entity;

import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.todo.dto.request.UpdateTodoRequestDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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

    private String content;

    private LocalDateTime endDate;

    public void updateTodo(UpdateTodoRequestDto updateTodoRequestDto) {
        this.title = updateTodoRequestDto.getTitle();
        this.content = updateTodoRequestDto.getContent();
        this.endDate = updateTodoRequestDto.getEndDate();
    }
}
