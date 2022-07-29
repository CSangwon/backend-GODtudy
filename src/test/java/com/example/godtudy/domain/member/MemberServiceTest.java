package com.example.godtudy.domain.member;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.dto.request.profile.FindPasswordRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.FindUsernameRequestDto;
import com.example.godtudy.domain.member.dto.response.profile.FindUsernameResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.global.advice.exception.MemberNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.*;

import static org.assertj.core.api.Assertions.*;


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
        List<SubjectEnum> subjectEnums = new ArrayList<>();
        subjectEnums.add(SubjectEnum.BIOLOGY);
        subjectEnums.add(SubjectEnum.CHEMISTRY);
        MemberJoinForm memberJoinForm = MemberJoinForm.builder()
                .username("swchoi1997")
                .password("tkddnjs4371@")
                .name("최상원")
                .email("swchoi1997@naver.com")
                .nickname("숲속의냉면")
                .year("1997").month("02").day("12")
                .subject(subjectEnums)
                .build();
        memberService.initJoinMember(memberJoinForm, "student");
    }

    @AfterEach
    void afterEach(){
        memberRepository.deleteAll();
    }


    @DisplayName("학생 회원가입 - 정상")
    @Test
    public void joinStudent() throws Exception{
        //given
        MemberJoinForm newMember = MemberJoinForm.builder()
                .username("test40")
                .password("tkddnjs4371@")
                .name("최상원")
                .email("test40@naver.com")
                .nickname("test40")
                .subject(new ArrayList<SubjectEnum>(Arrays.asList(SubjectEnum.CHEMISTRY, SubjectEnum.BIOLOGY)))
                .year("1997").month("02").day("12")
                .build();
        //when
        memberService.initJoinMember(newMember, "student");

        //then
        Member member = memberRepository.findByUsername(newMember.getUsername()).orElseThrow(() -> new Exception("유저가 없습니다."));

        assertThat(member.getId()).isNotNull();
        assertThat(member.getUsername()).isEqualTo(newMember.getUsername());
        assertThat(member.getName()).isEqualTo(newMember.getName());
        assertThat(member.getEmail()).isEqualTo(newMember.getEmail());
        assertThat(member.getNickname()).isEqualTo(newMember.getNickname());
        assertThat(passwordEncoder.matches("tkddnjs4371@", member.getPassword())).isTrue();

    }

    @DisplayName("아이디 찾기")
    @WithMember("test123")
    @Test
    public void findUsername() throws Exception{
        //given
        FindUsernameRequestDto findUsernameRequestDto = FindUsernameRequestDto.builder().name("최상원").email("test123@naver.com").build();
        //when
        FindUsernameResponseDto findUsernameResponseDto = memberService.findUsername(findUsernameRequestDto);

        //then
        assertThat(findUsernameResponseDto).isNotNull();
        assertThat(findUsernameResponseDto.getUsername()).isEqualTo("test123");
    }

    @DisplayName("아이디 찾기 - 실패 이름오류")
    @WithMember("test123")
    @Test
    public void findUsernameFailByName() throws Exception{
        //given
        FindUsernameRequestDto findUsernameRequestDto = FindUsernameRequestDto.builder().name("유하연").email("test123@naver.com").build();

        //when, then
        MemberNotFoundException memberNotFoundException = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.findUsername(findUsernameRequestDto);
        });

        assertThat(memberNotFoundException.getMessage()).isEqualTo("존재하지 않는 회원정보입니다.");
    }

    @DisplayName("아이디 찾기 - 실패 이메일오류")
    @WithMember("test123")
    @Test
    public void findUsernameFailByEmail() throws Exception{
        //given
        FindUsernameRequestDto findUsernameRequestDto = FindUsernameRequestDto.builder().name("최상원").email("123test@naver.com").build();

        //when, then
        MemberNotFoundException memberNotFoundException = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.findUsername(findUsernameRequestDto);
        });

        assertThat(memberNotFoundException.getMessage()).isEqualTo("존재하지 않는 회원정보입니다.");
    }
    
    @DisplayName("비밀번호 찾기 - 성공")
    @WithMember("test123")
    @Test
    public void findPasswordSuccess() throws Exception{
        //given
        FindPasswordRequestDto findPasswordRequestDto = FindPasswordRequestDto.builder().name("최상원").username("test123").email("test123@naver.com").build();

        //when
        memberService.findPassword(findPasswordRequestDto);


        //then
        Member member = new Member();
        assertThat(member.getPassword()).isNotSameAs("qwer123!@#");
    }

    @DisplayName("비밀번호 찾기 - 실패 - 이름오류")
    @WithMember("test123")
    @Test
    public void findPasswordFailByName() throws Exception{
        //given
        FindPasswordRequestDto findPasswordRequestDto = FindPasswordRequestDto.builder().name("유하연").username("test123").email("test123@naver.com").build();

        //when
        MemberNotFoundException memberNotFoundException = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.findPassword(findPasswordRequestDto);
        });

        //then
        assertThat(memberNotFoundException.getMessage()).isEqualTo("존재하지 않는 회원정보입니다.");
    }


    @DisplayName("비밀번호 찾기 - 실패 - 아이디오류")
    @WithMember("test123")
    @Test
    public void findPasswordFailByUsername() throws Exception{
        //given
        FindPasswordRequestDto findPasswordRequestDto = FindPasswordRequestDto.builder().name("최상원").username("test12").email("test123@naver.com").build();

        //when
        MemberNotFoundException memberNotFoundException = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.findPassword(findPasswordRequestDto);
        });

        //then
        assertThat(memberNotFoundException.getMessage()).isEqualTo("존재하지 않는 회원정보입니다.");
    }

    @DisplayName("비밀번호 찾기 - 실패 - 이메일오류")
    @WithMember("test123")
    @Test
    public void findPasswordFailByEmail() throws Exception{
        //given
        FindPasswordRequestDto findPasswordRequestDto = FindPasswordRequestDto.builder().name("최상원").username("test123").email("test12@naver.com").build();

        //when
        MemberNotFoundException memberNotFoundException = Assertions.assertThrows(MemberNotFoundException.class, () -> {
            memberService.findPassword(findPasswordRequestDto);
        });

        //then
        assertThat(memberNotFoundException.getMessage()).isEqualTo("존재하지 않는 회원정보입니다.");
    }

//    @WithMember("test123")
//    @Test
//    public void nplus1_problem_check() throws Exception{
//        System.out.println("------------------------------------------");
//        System.out.println("------------------------------------------");
//        System.out.println("------------------------------------------");
//        memberRepository.findByUsername("test123").orElseThrow();
//        System.out.println("------------------------------------------");
//        System.out.println("------------------------------------------");
//        System.out.println("------------------------------------------");
//    }
}