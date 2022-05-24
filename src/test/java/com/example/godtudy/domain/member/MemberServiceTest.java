package com.example.godtudy.domain.member;

import com.example.godtudy.domain.member.dto.StudentJoinForm;
import com.example.godtudy.domain.member.entity.Member;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;


    @BeforeEach
    void beforeEach(){
        StudentJoinForm studentJoinForm = StudentJoinForm.builder()
                .username("swchoi1997")
                .password("tkddnjs4371@")
                .name("최상원")
                .email("swchoi1997@naver.com")
                .nickname("숲속의냉면")
                .year("1997").month("02").day("12")
                .build();
        memberService.joinMember(studentJoinForm);
    }

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
    }


    @DisplayName("학생 회원가입 - 정상")
    @Test
    public void joinStudent() throws Exception{
        //given
        StudentJoinForm newMember = StudentJoinForm.builder()
                .username("test1")
                .password("tkddnjs4371@")
                .name("최상원")
                .email("test1@naver.com")
                .nickname("test1")
                .year("1997").month("02").day("12")
                .build();
        //when
        memberService.joinMember(newMember);

        //then
        Member member = memberRepository.findByUsername(newMember.getUsername()).orElseThrow(() -> new Exception("유저가 없습니다."));

        assertThat(member.getId()).isNotNull();
        assertThat(member.getUsername()).isEqualTo(newMember.getUsername());
        assertThat(member.getName()).isEqualTo(newMember.getName());
        assertThat(member.getEmail()).isEqualTo(newMember.getEmail());
        assertThat(member.getNickname()).isEqualTo(newMember.getNickname());
        assertThat(passwordEncoder.matches(member.getPassword(), newMember.getPassword()));
    }

    @DisplayName("학생 회원가입 - 아이디 중복")
    @Test
    public void joinStudent_error_username() throws Exception{
        //given
        StudentJoinForm newMember = StudentJoinForm.builder()
                .username("swchoi1997")
                .password("tkddnjs4371@")
                .name("최상원")
                .email("test1@naver.com")
                .nickname("test1")
                .year("1997").month("02").day("12")
                .build();
        //when
        memberService.joinMember(newMember);

        //then


    }

}