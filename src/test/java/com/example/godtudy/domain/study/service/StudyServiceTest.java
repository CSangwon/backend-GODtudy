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
import org.springframework.transaction.annotation.Transactional;

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

    @BeforeEach
    void createStudy(){
        Member member = memberRepository.findByUsername("swchoi1997")
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        member.setRole(Role.TEACHER);

        CreateStudyRequestDto request = CreateStudyRequestDto.builder()
                .name("새로운 스터디")
                .url("new_study")
                .studentId(1L)
                .subject("영어")
                .shortDescription("안녕하세요! 이건 새로운 스터디 입니다")
                .build();

        studyService.createStudyByTeacher(member, request);
    }

    @DisplayName("스터디 생성 by teacher 성공")
    @WithMember("swchoi1997")
    @Test
    public void 스터디_생성() throws Exception {
        //given
        Member member = memberRepository.findByUsername("swchoi1997")
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        member.setRole(Role.TEACHER);

        CreateStudyRequestDto request = CreateStudyRequestDto.builder()
                                        .name("수학 스터디")
                                        .url("math_study")
                                        .studentId(4L)
                                        .subject("수학")
                                        .shortDescription("안녕하세요! 이건 수학 스터디 입니다")
                                        .build();

        //when
        studyService.createStudyByTeacher(member, request);
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
        Member member = memberRepository.findById(2L)
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        String url = "new_study";
        UpdateStudyRequestDto request = UpdateStudyRequestDto.builder()
                .name("국어 스터디")
                .shortDescription("새로운 스터디에서 국어 스터디로 변경합니다~!")
                .build();

        //when //then
        assertThrows(AccessDeniedException.class, () ->
                studyService.updateStudy(member, url, request));

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