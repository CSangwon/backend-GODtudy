package com.example.godtudy.domain.member.controller;

import com.example.godtudy.ApiDocumentUtils;
import com.example.godtudy.domain.member.dto.request.EmailRequestDto;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.dto.request.NicknameRequestDto;
import com.example.godtudy.domain.member.dto.request.UsernameRequestDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


import java.util.ArrayList;
import java.util.Arrays;

import static com.example.godtudy.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class MemberApiControllerTest {

    private final static String BASE_URL = "/api/member";

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }



    @DisplayName("회원가입")
    @Test
    public void joinMemberSuccess() throws Exception{
        //given
        MemberJoinForm newMember = MemberJoinForm.builder()
                .username("test40")
                .password("tkddnjs4371@")
                .passwordConfirm("tkddnjs4371@")
                .name("최상원")
                .email("test40@naver.com")
                .nickname("test40")
                .subject(new ArrayList<String>(Arrays.asList("CHEMISTRY", "BIOLOGY")))
                .year("1997").month("02").day("12")
                .build();

        String url = BASE_URL + "/join/";
        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(newMember);

        mockMvc.perform(RestDocumentationRequestBuilders.post(url + "{role}", "student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("member-join-success",
                                getDocumentRequest(),
                                pathParameters(
                                        parameterWithName("role").description("스터디 url")
                                ),
                                requestFields(
                                        fieldWithPath("username").description("사용자 아이디"),
                                        fieldWithPath("password").description("사용자 비밀번호"),
                                        fieldWithPath("passwordConfirm").description("사용자 비밀번호 확인"),
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("nickname").description("사용자 닉네임"),
                                        fieldWithPath("subject").description("사용자 관심과목"),
                                        fieldWithPath("year").description("사용자 출생년도"),
                                        fieldWithPath("month").description("사용자 출생월"),
                                        fieldWithPath("day").description("사용자 출생일")
                                )
                        )
                );
    }

    @DisplayName("이메일 인증")
    @Test
    public void checkEmailToken() throws Exception{

        String token = initJoin_TmpMember();
        String url = BASE_URL + "/check-email-token";

        mockMvc.perform(RestDocumentationRequestBuilders.get(url + "?token=" + token + "&email=test40@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("tmp-member-check-email-valid",requestParameters(
                                parameterWithName("token").description("이메일 인증토큰"),
                                parameterWithName("email").description("인증해야할 이메일")
                        )));

        memberRepository.delete(memberRepository.findByUsername("test40").orElseThrow());
    }

    @DisplayName("아이디 중복 확인")
    @Test
    public void checkUsernameValid() throws Exception{
        UsernameRequestDto usernameRequestDto = new UsernameRequestDto();
        usernameRequestDto.setUsername("test40");

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(usernameRequestDto);

        String url = BASE_URL + "/join/username-check";
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("check-username-valid",
                        getDocumentRequest(),
                        requestFields(
                                fieldWithPath("username").description("중복 확인하려는 사용자 아이디")
                        )
                ));
    }

    @DisplayName("이메일 중복 확인")
    @Test
    public void checkEmailValid() throws Exception {
        EmailRequestDto emailRequestDto = new EmailRequestDto();
        emailRequestDto.setEmail("test40@naver.com");

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(emailRequestDto);

        String url = BASE_URL + "/join/email-check";
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("check-email-valid",
                        getDocumentRequest(),
                        requestFields(
                                fieldWithPath("email").description("중복 확인하려는 사용자 이메일")
                        ))
                );
    }

    @DisplayName("닉네임 중복 확인")
    @Test
    public void checkNicknameValid() throws Exception {
        NicknameRequestDto nicknameRequestDto = new NicknameRequestDto();
        nicknameRequestDto.setNickname("forest_choi");

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(nicknameRequestDto);

        String url = BASE_URL + "/join/nickname-check";
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("check-nickname-valid",
                        getDocumentRequest(),
                        requestFields(
                                fieldWithPath("nickname").description("중복 확인하려는 사용자 닉네임")
                        ))
                );
    }

    private String initJoin_TmpMember(){
        MemberJoinForm newMember = MemberJoinForm.builder()
                .username("test40")
                .password("tkddnjs4371@")
                .passwordConfirm("tkddnjs4371@")
                .name("최상원")
                .email("test40@naver.com")
                .nickname("test40")
                .subject(new ArrayList<String>(Arrays.asList("CHEMISTRY", "BIOLOGY")))
                .year("1997").month("02").day("12")
                .build();
        memberService.initJoinMember(newMember, "student");
        return memberRepository.findByUsername("test40").orElseThrow().getEmailCheckToken();

    }
    
    

}

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
