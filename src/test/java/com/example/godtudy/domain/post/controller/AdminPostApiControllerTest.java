package com.example.godtudy.domain.post.controller;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
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
import java.util.List;

import static com.example.godtudy.ApiDocumentUtils.getDocumentRequest;
import static com.example.godtudy.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class AdminPostApiControllerTest {

    private final static String BASE_URL = "/api/admin";

    private MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AdminPostRepository adminPostRepository;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @AfterEach
    public void after() {
        memberRepository.delete(memberRepository.findByUsername("swchoi1997").orElseThrow());
    }

    private Long createPost() {
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test").content("test").build();
        AdminPost adminPost = postSaveRequestDto.adminPostToEntity();
        adminPost.setAuthor(memberRepository.findByUsername("swchoi1997").orElseThrow());
        adminPost.setPostEnum("notice");
        adminPostRepository.save(adminPost);
        return adminPost.getId();
    }

    private MockMultipartFile getMockUploadFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/file/GODtudy_logo_2.png");
        return new MockMultipartFile("file", "file.png", "image/png",
                new FileInputStream(resource.getFile().getAbsolutePath()));
    }

    private void deleteFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        file.delete();
    }

    @WithMember("swchoi1997")
    @DisplayName("관리자 게시글 작성")
    @Test
    public void createAdminPost() throws Exception {
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(postSaveRequestDto);
//        String gsonString = objectMapper.writeValueAsString()
        MockMultipartFile json = new MockMultipartFile("postSaveRequestDto", "postSaveRequestDto", "application/json", gsonString.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(RestDocumentationRequestBuilders.multipart(BASE_URL + "/{post}/new", "notice")
                        .file(json).file(getMockUploadFile())

                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(status().isOk())
                .andDo(document("adminPost/adminPost-create",
                                pathParameters(
                                        parameterWithName("post").description("관리자 게시판 카테고리")
                                ), requestParts(
                                        partWithName("file").description("업로드 하고싶은 파일"),
                                        partWithName("postSaveRequestDto").description("제목, 내용")
                                )


                        )
                );
    }

    @WithMember("swchoi1997")
    @DisplayName("관리자 게시글 수정")
    @Test
    public void updateAdminPost() throws Exception {
        Long post = createPost();
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test123").content("test123").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(postUpdateRequestDto);
        MockMultipartFile json = new MockMultipartFile("postUpdateRequestDto", "postUpdateRequestDto", "application/json", gsonString.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(RestDocumentationRequestBuilders.multipart(BASE_URL + "/{post}/{postId}", "notice", post)
                        .file(json).file(getMockUploadFile())

                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(status().isOk())
                .andDo(document("adminPost/adminPost-update",
                                pathParameters(
                                        parameterWithName("post").description("관리자 게시판 카테고리"),
                                        parameterWithName("postId").description("수정하려는 게시글 아이디")
                                ), requestParts(
                                        partWithName("file").description("업로드 하고싶은 파일"),
                                        partWithName("postUpdateRequestDto").description("제목, 내용")
                                )
                        )
                );
        adminPostRepository.delete(adminPostRepository.findById(post).orElseThrow());
    }

    @WithMember("swchoi1997")
    @DisplayName("관리자 게시글 삭제")
    @Test
    public void deleteAdminPost() throws Exception {
        Long post = createPost();

        mockMvc.perform(delete(BASE_URL)
                        .header("Post", "notice")
                        .header("Post-Id", post)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("adminPost/adminPost-delete",
                                requestHeaders(
                                        headerWithName("Post").description("관리자 게시판 카테고리"),
                                        headerWithName("Post-Id").description("삭제하려는 게시글 아이디")
                                )
                        )
                );
    }

    @WithMember("swchoi1997")
    @DisplayName("관리자 게시글 조회")
    @Test
    public void getAdminPostInfo() throws Exception {
        Long post = createPost();

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{post}/{postId}", "notice", post)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("adminPost/adminPost-Info",
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("post").description("관리자 게시판 카테고리"),
                                        parameterWithName("postId").description("조회하려는 게시글 아이디")
                                ),
                                responseFields(
                                        fieldWithPath("postId").description("조회한 게시글 아이디"),
                                        fieldWithPath("title").description("조회한 게시글 제목"),
                                        fieldWithPath("content").description("조회한 게시글 내용"),
                                        fieldWithPath("author").description("조회한 게시글 작성자"),
                                        fieldWithPath("files").description("조회한 게시글 첨부파일"),
                                        fieldWithPath("commentInfoResponseDtoList").description("조회한 게시글 댓글들")
                                )
                        )
                );
        adminPostRepository.delete(adminPostRepository.findById(post).orElseThrow());
    }

    @WithMember("swchoi1997")
    @DisplayName("관리자 게시판 조회")
    @Test
    public void getAdminPostList() throws Exception{
        Long post = createPost();

        mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + "/{post}?title=&content=", "notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("adminPost/adminPost-List",
                                getDocumentResponse(),
                                requestParameters(
                                        parameterWithName("title").description("검색하려는 게시글 제목 : Null허용"),
                                        parameterWithName("content").description("검색하려는 게시글 내용 : Null허용")
                                ),
                                pathParameters(
                                        parameterWithName("post").description("관리자 게시판 카테고리")
                                ),
                                responseFields(
                                            fieldWithPath("totalPageCount").description("전체 페이지 수"),
                                            fieldWithPath("currentPageNum").description("현재 페이지 번호"),
                                            fieldWithPath("totalElementCount").description("전체 게시글 갯수"),
                                            fieldWithPath("currentPageElementCount").description("현재 페이지에 존재하는 게시글 수"),
                                            fieldWithPath("simplePostDtoList").type(JsonFieldType.ARRAY).description("간단한 게시글 정보"),
                                            fieldWithPath("simplePostDtoList.[].postId").type(JsonFieldType.NUMBER).description("게시글 아이디"),
                                            fieldWithPath("simplePostDtoList.[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                            fieldWithPath("simplePostDtoList.[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                            fieldWithPath("simplePostDtoList.[].author").type(JsonFieldType.STRING).description("게시글 작성자"),
                                            fieldWithPath("simplePostDtoList.[].createdDate").type(JsonFieldType.STRING).description("게시글 생성시간")
                                )
                        )
                );
        adminPostRepository.delete(adminPostRepository.findById(post).orElseThrow());
    }

}