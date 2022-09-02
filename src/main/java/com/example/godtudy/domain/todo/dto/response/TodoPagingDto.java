package com.example.godtudy.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoPagingDto {

    private int totalPageCount;

    private int currentPageNumber;

    private Long totalElementCount;

    private int currentPageElementCount;

    private List<TodoDto> todoDtoList = new ArrayList<>();

    public TodoPagingDto(Page<TodoDto> todoDtoPage) {
        this.totalPageCount = todoDtoPage.getTotalPages();
        this.currentPageNumber = todoDtoPage.getNumber();
        this.totalElementCount = todoDtoPage.getTotalElements();
        this.currentPageElementCount = todoDtoPage.getNumberOfElements();
        this.todoDtoList = todoDtoPage.getContent();
    }
}
