package com.example.godtudy.domain.todo.service;

import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import com.example.godtudy.domain.todo.dto.request.CreateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.request.UpdateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.response.TodoDto;
import com.example.godtudy.domain.todo.entity.Todo;
import com.example.godtudy.domain.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TodoService {

    private final StudyRepository studyRepository;
    private final TodoRepository todoRepository;

    private Study findStudyByUrl(String studyUrl) {
        return studyRepository.findByUrl(studyUrl);
    }

    private Todo findTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당하는 todo 정보가 없습니다."));
    }

    private TodoDto TodoEntityToDto(Todo todo) {
        return new TodoDto(todo);
    }

    @Transactional
    public TodoDto createTodo(String studyUrl, CreateTodoRequestDto createTodoRequestDto) {
        Study study = findStudyByUrl(studyUrl);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDate = LocalDateTime.parse(createTodoRequestDto.getEndDate(), formatter);

        Todo todo = Todo.builder()
                .title(createTodoRequestDto.getTitle())
                .content(createTodoRequestDto.getContent())
                .endDate(endDate)
                .study(study)
                .build();

        Todo savedTodo = todoRepository.save(todo);
        study.getTodoList().add(savedTodo);
        TodoDto todoDto = TodoEntityToDto(savedTodo);
        return todoDto;
    }

    @Transactional
    public Long updateTodo(UpdateTodoRequestDto updateTodoRequestDto) {
        Todo todo = findTodoById(updateTodoRequestDto.getId());

        todo.updateTodo(updateTodoRequestDto);
        Todo updateTodo = todoRepository.save(todo);

        return updateTodo.getId();
    }

    @Transactional
    public void deleteTodo(String studyUrl, Long todoId) {
        Todo todo = findTodoById(todoId);
        Study study = findStudyByUrl(studyUrl);
        study.getTodoList().remove(todo);
        todoRepository.delete(todo);
    }

    public TodoDto getTodo(Long todoId) {
        Todo todo = findTodoById(todoId);
        return TodoEntityToDto(todo);
    }

    public Page<TodoDto> getTodos(String studyUrl, Pageable pageable) {
        Study study = findStudyByUrl(studyUrl);
        Page<Todo> todoPage = todoRepository.findTodoByStudy(study, pageable);
        Page<TodoDto> todoDtoPage = todoPage.map(entity -> new TodoDto(entity));
        return todoDtoPage;
    }
}
