package com.example.godtudy.domain.study.controller;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.dto.request.UpdateStudyRequestDto;
import com.example.godtudy.domain.study.dto.response.StudyDto;
import com.example.godtudy.domain.study.service.StudyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static com.example.godtudy.ApiDocumentUtils.getDocumentRequest;
import static com.example.godtudy.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class StudyApiControllerTest {

//    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyService studyService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @WithMember("swchoi1997")
    @Test
    public void createStudy() throws Exception {

        //given
        StudyDto.Teacher teacher = StudyDto.Teacher.builder()
                .id(1L)
                .name("선생님 name")
                .username("선생님 username")
                .build();

        StudyDto.Student student = StudyDto.Student.builder()
                .id(2L)
                .name("학생 name")
                .username("학생 username")
                .build();

        StudyDto studyDto = StudyDto.builder()
                .name("국어 스터디")
                .url("korean-study")
                .shortDescription("국어 문법 스터디 입니다")
                .subject("국어")
                .teacher(teacher)
                .student(student)
                .build();

        given(studyService.createStudyByTeacher(any(Member.class), any(CreateStudyRequestDto.class)))
                .willReturn(studyDto);


        //when
        CreateStudyRequestDto createStudyRequestDto = CreateStudyRequestDto.builder()
                .name("국어")
                .url("korean-study")
                .shortDescription("국어 문법 스터디 입니다")
                .subject("국어")
                .teacherId(1L)
                .studentId(2L)
                .build();

        ResultActions result = this.mockMvc.perform(post("/api/study")
                .content(objectMapper.writeValueAsString(createStudyRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("study-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("url").type(JsonFieldType.STRING).description("스터디 url"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("과목"),
                                fieldWithPath("shortDescription").type(JsonFieldType.STRING).description("스터디 소개"),
                                fieldWithPath("teacherId").type(JsonFieldType.NUMBER).description("선생님 id"),
                                fieldWithPath("studentId").type(JsonFieldType.NUMBER).description("학생 id")
                        ),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("url").type(JsonFieldType.STRING).description("스터디 url"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("과목"),
                                fieldWithPath("shortDescription").type(JsonFieldType.STRING).description("스터디 소개"),
                                fieldWithPath("teacher.id").type(JsonFieldType.NUMBER).description("선생님 ID"),
                                fieldWithPath("teacher.name").type(JsonFieldType.STRING).description("선생님 이름"),
                                fieldWithPath("teacher.username").type(JsonFieldType.STRING).description("선생님 닉네임"),
                                fieldWithPath("student.id").type(JsonFieldType.NUMBER).description("학생 ID"),
                                fieldWithPath("student.name").type(JsonFieldType.STRING).description("학생 이름"),
                                fieldWithPath("student.username").type(JsonFieldType.STRING).description("학생 닉네임")
                        ))
                );
    }

    @WithMember("swchoi1997")
    @Test
    public void updateStudy() throws Exception {
        //given
        StudyDto.Teacher teacher = StudyDto.Teacher.builder()
                .id(1L)
                .name("선생님 name")
                .username("선생님 username")
                .build();

        StudyDto.Student student = StudyDto.Student.builder()
                .id(2L)
                .name("학생 name")
                .username("학생 username")
                .build();

        StudyDto studyDto = StudyDto.builder()
                .name("국어 스터디")
                .url("korean-study")
                .shortDescription("국어 문법 스터디입니다.")
                .subject("국어")
                .teacher(teacher)
                .student(student)
                .build();

        given(studyService.updateStudy(any(Member.class), ArgumentMatchers.anyString(), any(UpdateStudyRequestDto.class)))
                .willReturn(studyDto);

        //when
        UpdateStudyRequestDto updateStudyRequestDto = UpdateStudyRequestDto.builder()
                .name("국어 스터디")
                .shortDescription("국어 문법 스터디에서 다른 스터디로 변경할게요!")
                .build();

        ResultActions result = this.mockMvc.perform(put("/api/study/{studyUrl}", "korean-study")
                .content(objectMapper.writeValueAsString(updateStudyRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );
        //then
        result.andExpect(status().isOk())
                .andDo(document("study-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyUrl").description("스터디 url")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("스터디 이름"),
                                fieldWithPath("shortDescription").type(JsonFieldType.STRING).description("스터디 소개")
                        ),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("url").type(JsonFieldType.STRING).description("스터디 url"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("과목"),
                                fieldWithPath("shortDescription").type(JsonFieldType.STRING).description("스터디 소개"),
                                fieldWithPath("teacher.id").type(JsonFieldType.NUMBER).description("선생님 ID"),
                                fieldWithPath("teacher.name").type(JsonFieldType.STRING).description("선생님 이름"),
                                fieldWithPath("teacher.username").type(JsonFieldType.STRING).description("선생님 닉네임"),
                                fieldWithPath("student.id").type(JsonFieldType.NUMBER).description("학생 ID"),
                                fieldWithPath("student.name").type(JsonFieldType.STRING).description("학생 이름"),
                                fieldWithPath("student.username").type(JsonFieldType.STRING).description("학생 닉네임")
                        ))
                );

    }

    @WithMember("swchoi1997")
    @Test
    public void getStudy() throws Exception {
        //given
        StudyDto.Teacher teacher = StudyDto.Teacher.builder()
                .id(1L)
                .name("선생님 name")
                .username("선생님 username")
                .build();

        StudyDto.Student student = StudyDto.Student.builder()
                .id(2L)
                .name("학생 name")
                .username("학생 username")
                .build();

        StudyDto studyDto = StudyDto.builder()
                .name("국어 스터디")
                .url("korean-study")
                .shortDescription("국어 문법 스터디입니다.")
                .subject("국어")
                .teacher(teacher)
                .student(student)
                .build();

        given(studyService.getStudy(any(Member.class), ArgumentMatchers.anyString()))
                .willReturn(studyDto);

        //when
        ResultActions result = this.mockMvc.perform(get("/api/study/{studyUrl}", "korean-study")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("study-get-one",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyUrl").description("스터디 url")
                        ),
                        responseFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("url").type(JsonFieldType.STRING).description("스터디 url"),
                                fieldWithPath("subject").type(JsonFieldType.STRING).description("과목"),
                                fieldWithPath("shortDescription").type(JsonFieldType.STRING).description("스터디 소개"),
                                fieldWithPath("teacher.id").type(JsonFieldType.NUMBER).description("선생님 ID"),
                                fieldWithPath("teacher.name").type(JsonFieldType.STRING).description("선생님 이름"),
                                fieldWithPath("teacher.username").type(JsonFieldType.STRING).description("선생님 닉네임"),
                                fieldWithPath("student.id").type(JsonFieldType.NUMBER).description("학생 ID"),
                                fieldWithPath("student.name").type(JsonFieldType.STRING).description("학생 이름"),
                                fieldWithPath("student.username").type(JsonFieldType.STRING).description("학생 닉네임")
                        ))
                );
    }




}