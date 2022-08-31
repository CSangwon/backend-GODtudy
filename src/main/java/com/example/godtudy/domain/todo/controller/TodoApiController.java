package com.example.godtudy.domain.todo.controller;

import com.example.godtudy.domain.todo.dto.request.CreateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.request.UpdateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.response.TodoDto;
import com.example.godtudy.domain.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/study")
@RequiredArgsConstructor
@RestController
public class TodoApiController {

    private final TodoService todoService;

    @GetMapping("/{studyUrl}/todo/{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable("id") Long todoId) {
        TodoDto todo = todoService.getTodo(todoId);
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @GetMapping("/{studyUrl}/todo")
    public ResponseEntity<Page<TodoDto>> getTodos(
            @PathVariable("studyUrl") String studyUrl,
            @PageableDefault(size = 12, sort = "endDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TodoDto> todoDtoPage = todoService.getTodos(studyUrl, pageable);
        return new ResponseEntity<>(todoDtoPage, HttpStatus.OK);
    }

    @PostMapping("/{studyUrl}/todo")
    public ResponseEntity<TodoDto> createTodo(
            @PathVariable("studyUrl") String studyUrl,
            @RequestBody CreateTodoRequestDto createTodoRequestDto) {
        TodoDto todoDto = todoService.createTodo(studyUrl, createTodoRequestDto);
        return new ResponseEntity<>(todoDto, HttpStatus.OK);
    }

    @PutMapping("/{studyUrl}/todo/{id}")
    public ResponseEntity<TodoDto> updateTodo(
            @PathVariable("id") Long todoId,
            @RequestBody UpdateTodoRequestDto updateTodoRequestDto) {
        TodoDto updateTodoDto = todoService.updateTodo(todoId, updateTodoRequestDto);
        return new ResponseEntity<>(updateTodoDto, HttpStatus.OK);
    }

    @DeleteMapping("{studyUrl}/todo/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable("studyUrl") String studyUrl,
            @RequestHeader("Todo-id") Long todoId) {
        todoService.deleteTodo(studyUrl, todoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
