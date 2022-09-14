package com.example.godtudy.domain.member.controller;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.dto.request.profile.PasswordUpdateRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.member.dto.response.profile.ProfilePagingDto;
import com.example.godtudy.domain.member.dto.response.profile.ProfileResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

    @WithMember("swchoi1997")
    @Test
    public void updatePassword() throws Exception {
        //given
        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .newPassword("newpassword1@")
                .newPasswordConfirm("newpassword1@")
                .build();

        //when
        ResultActions result = this.mockMvc.perform(post("/api/profile/password")
                .header("X-AUTH-TOKEN", "jwtToken")
                .content(objectMapper.writeValueAsString(passwordUpdateRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("profile/password-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("access token")
                        ),
                        requestFields(
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("변경하려고 하는 password"),
                                fieldWithPath("newPasswordConfirm").type(JsonFieldType.STRING).description("변경하려고 하는 password 확인")
                        )
                ));
    }

    @WithMember("swchoi1997")
    @Test
    public void getProfileList() throws Exception {
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

        List<ProfileResponseDto> profileDtoList = new ArrayList<>();
        profileDtoList.add(profileResponseDto);

        Pageable pageable = PageRequest.of(0, 10);

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), profileDtoList.size());
        Page<ProfileResponseDto> profileResponseDtoPage = new PageImpl<>(profileDtoList.subList(start, end), pageable, profileDtoList.size());

        ProfilePagingDto profilePagingDto = new ProfilePagingDto(profileResponseDtoPage);

        given(profileService.searchMember(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), any(Role.class), any(PageRequest.class)))
                .willReturn(profilePagingDto);

        //when
        ResultActions result = this.mockMvc.perform(get("/api/profile/list")
                .param("username", "")
                .param("role", "STUDENT")
                .param("name", "")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("profile/profile-get-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("username").description("username"),
                                parameterWithName("name").description("name"),
                                parameterWithName("role").description("role")
                        ),
                        responseFields(
                                fieldWithPath("totalPageCount").type(JsonFieldType.NUMBER).description("전체 page의 수"),
                                fieldWithPath("currentPageNumber").type(JsonFieldType.NUMBER).description("현재 page의 수"),
                                fieldWithPath("totalElementCount").type(JsonFieldType.NUMBER).description("전체 content의 개수"),
                                fieldWithPath("currentPageElementCount").type(JsonFieldType.NUMBER).description("한 페이지당 content의 개수"),
                                fieldWithPath("profileDtoList.[].username").type(JsonFieldType.STRING).description("회원 username"),
                                fieldWithPath("profileDtoList.[].email").type(JsonFieldType.STRING).description("회원 email"),
                                fieldWithPath("profileDtoList.[].nickname").type(JsonFieldType.STRING).description("회원 nickname"),
                                fieldWithPath("profileDtoList.[].bio").type(JsonFieldType.STRING).description("회원 자기소개"),
                                fieldWithPath("profileDtoList.[].profileImageUrl").type(JsonFieldType.STRING).description("회원 profile image"),
                                fieldWithPath("profileDtoList.[].subjectList").type(JsonFieldType.ARRAY).description("회원 subject list")
                        )
                ));


    }


}