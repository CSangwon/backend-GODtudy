package com.example.godtudy.domain.todo.repository;

import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findTodoByStudy(Study study);
}
