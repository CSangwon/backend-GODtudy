package com.example.godtudy.domain.post.controller;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.StudyPost;
import com.example.godtudy.domain.post.repository.StudyPostRepository;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import com.example.godtudy.domain.study.service.StudyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.example.godtudy.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class StudyPostApiControllerTest {

    private final static String BASE_URL = "/api/study";

    private MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StudyPostRepository studyPostRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    StudyService studyService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        signMember();
    }

    @AfterEach
    public void after() {
        memberRepository.delete(memberRepository.findByUsername("swchoi1997").orElseThrow());

    }

    private Long createPost() {
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test").content("test").build();
        StudyPost studyPost = postSaveRequestDto.studyPostToEntity();
        studyPost.setAuthor(memberRepository.findByUsername("swchoi1997").orElseThrow());
        studyPost.setStudy(studyRepository.findByUrl("test"));
        studyPost.setPostEnum("homework");
        studyPostRepository.save(studyPost);
        return studyPost.getId();
    }

    private MockMultipartFile getMockUploadFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/file/GODtudy_logo_2.png");
        return new MockMultipartFile("file", "file.png", "image/png",
                new FileInputStream(resource.getFile().getAbsolutePath()));
    }

    private void signMember(){
        List<String> subjectEnums = new ArrayList<>();
        subjectEnums.add("BIOLOGY");
        subjectEnums.add("CHEMISTRY");
        MemberJoinForm memberJoinForm = MemberJoinForm.builder()
                .username("swchoi123")
                .password("tkddnjs4371@")
                .name("최상원")
                .email("swchoi123@naver.com")
                .nickname("숲속")
                .year("1997").month("02").day("12")
                .subject(subjectEnums)
                .build();
        memberService.initJoinMember(memberJoinForm, "student");

        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();
        member.setRole(Role.STUDENT);
    }

    private void createStudy(String username){
        Member teacher = memberRepository.findByUsername("test1").orElseThrow();
        teacher.setRole(Role.TEACHER);
        Member student = memberRepository.findByUsername(username).orElseThrow();
        student.setRole(Role.STUDENT);

        CreateStudyRequestDto createStudyRequestDto = CreateStudyRequestDto
                .builder().name("test").teacherId(teacher.getId())
                .studentId(student.getId()).url("test").subject("ENGLISH")
                .shortDescription("test123").build();

        studyService.createStudyByTeacher(teacher, createStudyRequestDto);
    }

    @WithMember("swchoi1997")
    @DisplayName("학습지 게시글 작성")
    @Test
    public void createStudyPost() throws Exception{
        createStudy("swchoi1997");
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("studyPost").content("studyPost").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(postSaveRequestDto);
        MockMultipartFile json = new MockMultipartFile("postSaveRequestDto", "postSaveRequestDto",
                "application/json", gsonString.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(RestDocumentationRequestBuilders.multipart(BASE_URL + "/{studyUrl}/post/{post}", "test", "homework")
                        .file(json).file(getMockUploadFile())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(status().isOk())
                .andDo(document("studyPost/studyPost-create",
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("studyUrl").description("스터디 고유 주소"),
                                        parameterWithName("post").description("스터디 게시판 카테고리")
                                ),
                                requestParts(
                                        partWithName("file").description("업로드 하고싶은 파일"),
                                        partWithName("postSaveRequestDto").description("제목, 내용")
                                ),
                                responseFields(
                                        fieldWithPath("response").type(JsonFieldType.OBJECT).description("응답 내용"),
                                        fieldWithPath("response.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("response.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("response.author").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("response.files").type(JsonFieldType.ARRAY).description("첨부파일"),
                                        fieldWithPath("response.postEnum").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("status").description("상태코드")
                                )
                        )
                );
    }

    @WithMember("swchoi1997")
    @DisplayName("학습지 게시글 수정")
    @Test
    public void updateStudyPost() throws Exception{
        createStudy("swchoi1997");
        Long post = createPost();

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test123").content("test123").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(postUpdateRequestDto);
        MockMultipartFile json = new MockMultipartFile("postUpdateRequestDto", "",
                "application/json", gsonString.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(RestDocumentationRequestBuilders.multipart(BASE_URL + "/{studyUrl}/post/{post}/{postId}",
                                        "test", "homework", post)
//                                .part(new MockPart("postUpdateRequestDto", gsonString.getBytes(StandardCharsets.UTF_8)))
                                .file(json)
                                .file(getMockUploadFile())
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andDo(document("studyPost/studyPost-update",
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("studyUrl").description("스터디 고유 주소"),
                                        parameterWithName("post").description("스터디 게시판 카테고리"),
                                        parameterWithName("postId").description("스터디 게시판 게시글 아이디")
                                ),
                                requestParts(
                                        partWithName("file").description("업로드 하고싶은 파일"),
                                        partWithName("postUpdateRequestDto").description("제목, 내용")
                                ),
                                responseFields(
                                        fieldWithPath("response").type(JsonFieldType.OBJECT).description("응답 내용"),
                                        fieldWithPath("response.title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("response.content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("response.author").type(JsonFieldType.STRING).description("작성자"),
                                        fieldWithPath("response.files").type(JsonFieldType.ARRAY).description("첨부파일"),
                                        fieldWithPath("response.postEnum").type(JsonFieldType.STRING).description("카테고리"),
                                        fieldWithPath("status").description("상태코드")
                                )


                        )
                );
    }

    @WithMember("swchoi1997")
    @DisplayName("학생 게시글 삭제")
    @Test
    public void deleteStudyPost() throws Exception{
        createStudy("swchoi1997");
        Long post = createPost();

        mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{studyUrl}/post/{post}", "test", "homework")
                        .header("Post-Id", post)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("studyPost/studyPost-delete",
                                requestHeaders(
                                        headerWithName("Post-Id").description("삭제하려는 게시글 아이디")
                                ),
                                pathParameters(
                                        parameterWithName("studyUrl").description("스터디 고유 주소"),
                                        parameterWithName("post").description("스터디 게시판 카테고리")
                                )
                        )
                );
    }


    @WithMember("swchoi1997")
    @DisplayName("스터디 게시글 조회")
    @Test
    public void getStudyPostInfo() throws Exception{
        createStudy("swchoi1997");
        Long post = createPost();

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{studyUrl}/post/{post}/{postId}"
                                , "test", "homework", post)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("studyPost/studyPost-info",
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("studyUrl").description("스터디 고유 주소"),
                                        parameterWithName("post").description("스터디 게시판 카테고리"),
                                        parameterWithName("postId").description("조회하려는 게시글 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("response").type(JsonFieldType.OBJECT).description("응답 내용"),
                                        fieldWithPath("response.postId").description("조회한 게시글 아이디"),
                                        fieldWithPath("response.title").description("조회한 게시글 제목"),
                                        fieldWithPath("response.content").description("조회한 게시글 내용"),
                                        fieldWithPath("response.author").description("조회한 게시글 작성자"),
                                        fieldWithPath("response.files").description("조회한 게시글 첨부파일"),
                                        fieldWithPath("response.commentInfoResponseDtoList").description("조회한 게시글 댓글들"),
                                        fieldWithPath("status").description("상태코드")
                                )
                        )
                );
    }

    @WithMember("swchoi1997")
    @DisplayName("스터디 게시판 조회")
    @Test
    public void getStudyPostList() throws Exception {
        createStudy("swchoi1997");
        Long post = createPost();

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{studyUrl}/post/{post}?title=&content="
                                , "test", "homework")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("studyPost/studyPost-List",
                                getDocumentResponse(),
                                requestParameters(
                                        parameterWithName("title").description("검색하려는 게시글 제목 : Null허용"),
                                        parameterWithName("content").description("검색하려는 게시글 내용 : Null허용")
                                ),
                                pathParameters(
                                        parameterWithName("studyUrl").description("스터디 고유 주소"),
                                        parameterWithName("post").description("스터디 게시판 카테고리")
                                ),
                                responseFields(
                                        fieldWithPath("response").type(JsonFieldType.OBJECT).description("응답 내용"),
                                        fieldWithPath("response.totalPageCount").description("전체 페이지 수"),
                                        fieldWithPath("response.currentPageNum").description("현재 페이지 번호"),
                                        fieldWithPath("response.totalElementCount").description("전체 게시글 갯수"),
                                        fieldWithPath("response.currentPageElementCount").description("현재 페이지에 존재하는 게시글 수"),
                                        fieldWithPath("response.simplePostDtoList").type(JsonFieldType.ARRAY).description("간단한 게시글 정보"),
                                        fieldWithPath("response.simplePostDtoList.[].postId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                        fieldWithPath("response.simplePostDtoList.[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("response.simplePostDtoList.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                        fieldWithPath("response.simplePostDtoList.[].author").type(JsonFieldType.STRING).description("게시글 작성자"),
                                        fieldWithPath("response.simplePostDtoList.[].createdDate").type(JsonFieldType.STRING).description("게시글 생성시간"),
                                        fieldWithPath("status").description("상태코드")
                                )
                        )
                );
    }
}