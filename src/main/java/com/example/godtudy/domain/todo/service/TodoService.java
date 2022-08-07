package com.example.godtudy.domain.todo.service;

import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import com.example.godtudy.domain.todo.dto.request.CreateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.response.TodoDto;
import com.example.godtudy.domain.todo.entity.Todo;
import com.example.godtudy.domain.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TodoService {

    private final StudyRepository studyRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public TodoDto createTodo(String studyUrl, CreateTodoRequestDto createTodoRequestDto) {
        Study study = studyRepository.findByUrl(studyUrl);

        Todo todo = Todo.builder()
                .title(createTodoRequestDto.getTitle())
                .content(createTodoRequestDto.getContent())
                .endDate(createTodoRequestDto.getEndDate())
                .study(study)
                .build();

        Todo savedTodo = todoRepository.save(todo);
        TodoDto todoDto = new TodoDto(savedTodo);

        return todoDto;
    }

}
