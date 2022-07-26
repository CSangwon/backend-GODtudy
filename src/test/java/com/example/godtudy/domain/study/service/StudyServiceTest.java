package com.example.godtudy.domain.study.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.dto.request.UpdateStudyRequestDto;
import com.example.godtudy.domain.study.dto.response.StudyDto;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class StudyServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StudyService studyService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                .nickname("nickname1")
                .birthday(LocalDate.of(2010,2,12))
                .role(Role.STUDENT)
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

    @DisplayName("스터디 생성 by teacher 성공")
    @WithMember("swchoi1997")
    @Test
    public void 스터디_생성() throws Exception {
        //given
        Member teacher = memberRepository.findByUsername("swchoi1997")
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        teacher.setRole(Role.TEACHER);

        Member student = Member.builder()
                .name("user2")
                .username("user2")
                .password(passwordEncoder.encode("user2"))
                .email("user2@gmail.com")
                .nickname("nickname2")
                .birthday(LocalDate.of(2010,2,12))
                .role(Role.STUDENT)
                .build();

        Member saveStudent = memberRepository.save(student);

        CreateStudyRequestDto request = CreateStudyRequestDto.builder()
                                        .name("수학 스터디")
                                        .url("math_study")
                                        .studentId(saveStudent.getId())
                                        .subject("수학")
                                        .shortDescription("안녕하세요! 이건 수학 스터디 입니다")
                                        .build();

        //when
        studyService.createStudyByTeacher(teacher, request);
        Study study = studyRepository.findByUrl(request.getUrl());

        //then
        assertEquals(request.getName(), study.getName());
        assertEquals(request.getUrl(), study.getUrl());
    }

    @DisplayName("스터디 수정")
    @WithMember("swchoi1997")
    @Test
    public void 스터디_수정() throws Exception {
        //given
        Member member = memberRepository.findByUsername("swchoi1997")
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        member.setRole(Role.TEACHER);
        String url = "new_study";
        UpdateStudyRequestDto request = UpdateStudyRequestDto.builder()
                .name("국어 스터디")
                .shortDescription("새로운 스터디에서 국어 스터디로 변경합니다~!")
                .build();

        //when
        StudyDto studyDto = studyService.updateStudy(member, url, request);
        Study study = studyRepository.findByUrl(studyDto.getUrl());

        //then
        assertEquals(study.getName(), request.getName());
        assertEquals(study.getShortDescription(), request.getShortDescription());
    }

    @DisplayName("스터디 수정 - 권한 없음")
    @WithMember("swchoi1997")
    @Test
    public void 스터디_수정_권한X() throws Exception {
        //given
        Member unauthorizedMember = Member.builder()
                .name("user3")
                .username("user3")
                .password(passwordEncoder.encode("user3"))
                .email("user3@gmail.com")
                .nickname("nickname3")
                .birthday(LocalDate.of(2010,3,20))
                .role(Role.TEACHER)
                .build();

        String url = "new_study";
        UpdateStudyRequestDto request = UpdateStudyRequestDto.builder()
                .name("국어 스터디")
                .shortDescription("새로운 스터디에서 국어 스터디로 변경합니다~!")
                .build();

        //when //then
        assertThrows(AccessDeniedException.class, () ->
                studyService.updateStudy(unauthorizedMember, url, request));

    }


    @DisplayName("스터디 삭제")
    @WithMember("swchoi1997")
    @Test
    public void 스터디_삭제() throws Exception {
        //given
        Member member = memberRepository.findByUsername("swchoi1997")
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        String url = "new_study";

        //when
        String deleteStudyUrl = studyService.deleteStudy(member, url);

        //then
        assertEquals(url, deleteStudyUrl);
    }
}