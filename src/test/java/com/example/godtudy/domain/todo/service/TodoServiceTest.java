package com.example.godtudy.domain.todo.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import com.example.godtudy.domain.todo.dto.request.CreateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.request.UpdateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.response.TodoDto;
import com.example.godtudy.domain.todo.entity.Todo;
import com.example.godtudy.domain.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TodoServiceTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;


    @BeforeEach
    void createStudy(){
        Member teacher = memberRepository.findByUsername("swchoi1997")
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

        teacher.setRole(Role.TEACHER);

        Member newStudent = Member.builder()
                .name("user1")
                .username("user1")
                .password(passwordEncoder.encode("user1"))
                .email("user1@gmail.com")
                .nickname("nickname-test")
                .birthday(LocalDate.of(2010, 2, 12))
                .role(Role.STUDENT)
                .subject(new ArrayList<>())
                .build();

        Member student = memberRepository.save(newStudent);

        Study study = Study.builder()
                .name("새로운 스터디")
                .url("new_study")
                .student(student)
                .teacher(teacher)
                .shortDescription("인녕하세요! 새로운 스터디에요!")
                .subject("영어")
                .build();

        studyRepository.save(study);
    }

    @WithMember("swchoi1997")
    @Test
    public void 할일_생성() throws Exception {
        //given
        String title = "국어 과제하기";
        String content = "p.201까지 꼼꼼하게 읽고 문제 풀어오기";
        String endDate = "2022-08-10 00:00:00";
        String studyUrl = "new_study";
        Study study = studyRepository.findByUrl(studyUrl);
        CreateTodoRequestDto createTodoRequestDto = CreateTodoRequestDto.builder()
                .title("국어 과제하기")
                .content(content)
                .endDate(endDate)
                .build();
        //when
        TodoDto todoDto = todoService.createTodo(studyUrl, createTodoRequestDto);

        //then
        assertEquals(todoDto.getTitle(), title);
        assertEquals(todoDto.getContent(), content);
        assertEquals(todoDto.getStudyUrl(), studyUrl);
        assertEquals(1, study.getTodoList().size());
    }

    @WithMember("swchoi1997")
    @Test
    public void 할일_수정() throws Exception {
        //given
        String studyUrl = "new_study";
        Study study = studyRepository.findByUrl(studyUrl);

        Todo todo = Todo.builder()
                .title("test 과제입니당")
                .content("p.200까지 수학과제 해야합니당")
                .study(study)
                .endDate(LocalDateTime.now())
                .build();

        Todo saveTodo = todoRepository.save(todo);

        String content = "p.190까지 해오는걸로 수정할게요";

        UpdateTodoRequestDto updateTodoRequestDto = UpdateTodoRequestDto.builder()
                .id(saveTodo.getId())
                .title(saveTodo.getTitle())
                .content(content)
                .endDate(saveTodo.getEndDate())
                .build();

        //when
        Long updateTodoId = todoService.updateTodo(updateTodoRequestDto);
        Todo updateTodo = todoRepository.findById(updateTodoId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 할  정보가 없습니다."));

        //then
        assertEquals(saveTodo.getId(), updateTodoId);
        assertEquals(content, updateTodo.getContent());
    }

    @WithMember("swchoi1997")
    @Test
    public void 할일_삭제() throws Exception {
        //given
        String studyUrl = "new_study";
        Study study = studyRepository.findByUrl(studyUrl);
        Todo todo = Todo.builder()
                .title("삭제할 과제입니당")
                .content("p.10까지 수학과제 해야합니당")
                .study(study)
                .endDate(LocalDateTime.now())
                .build();

        Todo saveTodo = todoRepository.save(todo);
        //when
        todoService.deleteTodo(studyUrl, saveTodo.getId());

        //then
        assertEquals(0, todo.getStudy().getTodoList().size());
        assertEquals(0, study.getTodoList().size());

    }

    @WithMember("swchoi1997")
    @Test
    public void 할일_단건조회() throws Exception {
        //given
        String studyUrl = "new_study";
        Study study = studyRepository.findByUrl(studyUrl);
        Todo todo = Todo.builder()
                .title("삭제할 과제입니당")
                .content("p.10까지 수학과제 해야합니당")
                .study(study)
                .endDate(LocalDateTime.now())
                .build();

        Todo saveTodo = todoRepository.save(todo);
        //when
        TodoDto todoDto = todoService.getTodo(saveTodo.getId());

        //then
        assertEquals(todo.getId(), todoDto.getId());
        assertEquals(todo.getTitle(), todoDto.getTitle());
        assertEquals(todo.getContent(), todoDto.getContent());
        assertEquals(todo.getEndDate(), todoDto.getEndDate());
        assertEquals(todo.getStudy().getUrl(), todoDto.getStudyUrl());

    }

    @WithMember("swchoi1997")
    @Test
    public void 할일_페이징_조회() throws Exception {
        //given
        String studyUrl = "new_study";
        Study study = studyRepository.findByUrl(studyUrl);
        Todo todo1 = Todo.builder()
                .title("new_todo1")
                .content("new_todo1_content")
                .study(study)
                .endDate(LocalDateTime.now())
                .build();

        Todo todo2 = Todo.builder()
                .title("new_todo2")
                .content("new_todo2_content")
                .study(study)
                .endDate(LocalDateTime.now())
                .build();

        Todo saveTodo1 = todoRepository.save(todo1);
        Todo saveTodo2 = todoRepository.save(todo2);

        //when
        Pageable pageable = PageRequest.of(1, 2);
        Page<TodoDto> todos = todoService.getTodos(studyUrl, pageable);

        //then
        assertEquals(2, todos.getSize());
        assertEquals(1, todos.getTotalPages());
    }


}