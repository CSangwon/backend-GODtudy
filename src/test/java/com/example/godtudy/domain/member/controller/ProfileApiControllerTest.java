package com.example.godtudy.domain.member.controller;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.member.dto.response.profile.ProfileResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.godtudy.ApiDocumentUtils.getDocumentRequest;
import static com.example.godtudy.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class ProfileApiControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileService profileService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @AfterEach
    public void after() {
        memberRepository.delete(memberRepository.findByUsername("swchoi1997").orElseThrow());
    }

    @WithMember("swchoi1997")
    @Test
    public void profileInquiry() throws Exception {
        //given
        List<SubjectEnum> subjectList = new ArrayList<>();

        subjectList.add(SubjectEnum.BIOLOGY);
        subjectList.add(SubjectEnum.KOREAN_LANGUAGE);

        ProfileResponseDto profileResponseDto = ProfileResponseDto.builder()
                .username("test username")
                .email("test@godtudy.com")
                .nickname("test nickname")
                .bio("test bio")
                .profileImageUrl("test profile image")
                .subjectList(subjectList).build();

        given(profileService.getProfile(any(Member.class)))
                .willReturn(profileResponseDto);

        //when
        ResultActions result = this.mockMvc.perform(get("/api/profile/{profileId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("profile/profile-get-one",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("profileId").description("profile의 id")
                        ),
                        responseFields(
                                fieldWithPath("username").type(JsonFieldType.STRING).description("회원의 username"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원의 email"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원의 nickname"),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("회원의 소개말"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("회원의 profileImageUrl"),
                                fieldWithPath("subjectList").type(JsonFieldType.ARRAY).description("회원의 subjectList")
                        )
                ));

    }

    @WithMember("swchoi1997")
    @Test
    public void updateProfile() throws Exception {
        //given
        List<String> subjectList = new ArrayList<>();

        subjectList.add("ENGLISH");
        subjectList.add("CHEMISTRY");

        ProfileRequestDto profileRequestDto = ProfileRequestDto.builder()
                .nickname("test nickname")
                .bio("test bio")
                .profileImageUrl("test profile image")
                .subjectList(subjectList).build();

        //when
        ResultActions result = this.mockMvc.perform(put("/api/profile")
                .content(objectMapper.writeValueAsString(profileRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("profile/profile-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원의 nickname"),
                                fieldWithPath("bio").type(JsonFieldType.STRING).description("회원의 자기소개"),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("회원의 profile Image"),
                                fieldWithPath("subjectList").type(JsonFieldType.ARRAY).description("회원의 subject List")
                        )
                ));
    }

}