//package com.example.godtudy.domain.member.api;
//
//import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
//import com.example.godtudy.domain.member.entity.Role;
//import com.example.godtudy.domain.member.entity.SubjectEnum;
//import com.example.godtudy.domain.member.repository.MemberRepository;
//import com.example.godtudy.domain.member.service.MemberService;
//import com.example.godtudy.global.config.SecurityConfig;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Transactional
//@SpringBootTest
//@AutoConfigureMockMvc
//class MemberApiControllerTest {
//
//
//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    MemberRepository memberRepository;
//    @Autowired
//    MemberService memberService;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//
//    @BeforeEach
//    void beforeEach(){
//        List<String> subject = new ArrayList<>();
//        subject.add("BIOLOGY");
//        subject.add("CHEMISTRY");
//
//        MemberJoinForm memberJoinForm = MemberJoinForm.builder()
//                .username("swchoi1997")
//                .password("tkddnjs4371@")
//                .name("최상원")
//                .email("swchoi1997@naver.com")
//                .nickname("숲속의냉면")
//                .year("1997").month("02").day("12")
//                .subject(subject)
//                .role(Role.STUDENT)
//                .build();
//        memberService.initJoinMember(memberJoinForm, "student");
//    }
//
//    @AfterEach
//    void afterEach(){
//        memberRepository.deleteAll();
//    }
//
//    @DisplayName("학생 회원가입 - 아이디 중복")
//    @Test
//    public void joinStudent_error_username() throws Exception{
//        mockMvc.perform(post("/api/member/join/student")
//                        .param("username", "swchoi1997")
//                        .param("password", "tkddnjs4371@")
//                        .param("name", "최상원")
//                        .param("email", "swchoi19972@naver.com")
//                        .param("nickname", "test1")
//                        .param("year", "1997")
//                        .param("month", "02")
//                        .param("day", "12"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @DisplayName("학생 회원가입 - 이메일 중복")
//    @Test
//    public void joinStudent_error_email() throws Exception{
//        mockMvc.perform(post("/api/member/join/student")
//                        .param("username", "swcho1i1997")
//                        .param("password", "tkddnjs4371@")
//                        .param("name", "최상원")
//                        .param("email", "swchoi1997@naver.com")
//                        .param("nickname", "test1")
//                        .param("year", "1997")
//                        .param("month", "02")
//                        .param("day", "12"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @DisplayName("학생 회원가입 - 닉네임 중복")
//    @Test
//    public void joinStudent_error_nickname() throws Exception{
//        mockMvc.perform(post("/api/member/join/student")
//                        .param("username", "swcho1i1997")
//                        .param("password", "tkddnjs4371@")
//                        .param("name", "최상원")
//                        .param("email", "swchoi19972@naver.com")
//                        .param("nickname", "숲속의 냉면")
//                        .param("year", "1997")
//                        .param("month", "02")
//                        .param("day", "12"))
//                .andExpect(status().isBadRequest());
//    }
//
//}
