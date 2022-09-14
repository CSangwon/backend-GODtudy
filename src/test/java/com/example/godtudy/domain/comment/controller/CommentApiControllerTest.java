package com.example.godtudy.domain.comment.controller;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.comment.dto.request.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.request.CommentUpdateDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
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

//import static com.example.godtudy.ApiDocumentUtils.*;
import static com.example.godtudy.ApiDocumentUtils.getDocumentRequest;
import static com.example.godtudy.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class CommentApiControllerTest {
    private final static String BASE_URL = "/api/comment";

    private MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AdminPostRepository adminPostRepository;

    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    public Long createPost() {
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test123123").content("test").build();
        AdminPost adminPost = postSaveRequestDto.adminPostToEntity();
        adminPost.setAuthor(memberRepository.findByUsername("swchoi1997").orElseThrow());
        adminPost.setPostEnum("notice");
        adminPostRepository.save(adminPost);
        return adminPost.getId();
    }

    @AfterEach
    public void after() {
        memberRepository.delete(memberRepository.findByUsername("swchoi1997").orElseThrow());
    }

    private Long createPComment(Member member, Long postId) {
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("qwerty").build();
        Comment pComment = commentSaveDto.toEntity();
        pComment.setAdminPost(adminPostRepository.findById(postId).orElseThrow());
        pComment.setWriter(member);
        commentRepository.save(pComment);
        return pComment.getId();
    }

    @DisplayName("댓글 생성")
    @WithMember("swchoi1997")
    @Test
    public void createParentComment() throws Exception {
        Long postId = createPost();
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test123").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(commentSaveDto);
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/{postType}/{postId}/new", "notice", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(document("comment/comment-create",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("postType").description("게시물 카테고리"),
                                        parameterWithName("postId").description("댓글을 작성하려는 게시물 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("댓글 내용")
                                ),
                                responseFields(
                                        fieldWithPath("response").description("응답내용"),
                                        fieldWithPath("response.id").description("댓글 아이디"),
                                        fieldWithPath("response.username").description("댓글 작성자 아이디"),
                                        fieldWithPath("response.content").description("댓글 내용"),
                                        fieldWithPath("response.postKind").description("해당 댓글이 있는 게시물의 카테고리"),
                                        fieldWithPath("response.message").description("응답 메세지"),
                                        fieldWithPath("status").description("상태코드")
                                )
                        )
                );
    }

    @DisplayName("대댓글 생성")
    @WithMember("swchoi1997")
    @Test
    public void createChildComment() throws Exception {
        Long postId = createPost();
        Long pCommentId = createPComment(memberRepository.findByUsername("swchoi1997").orElseThrow(), postId);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test123").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(commentSaveDto);
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/{postType}/{postId}/{parentId}/new", "notice", postId, pCommentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(document("comment/child-comment-create",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("postType").description("게시물 카테고리"),
                                        parameterWithName("postId").description("댓글을 작성하려는 게시물 아이디"),
                                        parameterWithName("parentId").description("부모 댓글 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("댓글 내용")
                                ),
                                responseFields(
                                        fieldWithPath("response").description("응답내용"),
                                        fieldWithPath("response.id").description("댓글 아이디"),
                                        fieldWithPath("response.username").description("댓글 작성자 아이디"),
                                        fieldWithPath("response.content").description("댓글 내용"),
                                        fieldWithPath("response.postKind").description("해당 댓글이 있는 게시물의 카테고리"),
                                        fieldWithPath("response.message").description("응답 메세지"),
                                        fieldWithPath("status").description("상태코드")
                                )
                        )
                );
    }

    @DisplayName("댓글 수정")
    @WithMember("swchoi1997")
    @Test
    public void updateComment() throws Exception {
        Long postId = createPost();
        Long commentId = createPComment(memberRepository.findByUsername("swchoi1997").orElseThrow(), postId);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder().content("test321").build();

        String gsonString = new GsonBuilder().setPrettyPrinting().create().toJson(commentUpdateDto);
        mockMvc.perform(RestDocumentationRequestBuilders.post(BASE_URL + "/{postType}/{postId}/{commentId}", "notice", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gsonString)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(document("comment/comment-update",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("postType").description("게시물 카테고리"),
                                        parameterWithName("postId").description("댓글을 작성하려는 게시물 아이디"),
                                        parameterWithName("commentId").description("댓글 아이디")
                                ),
                                requestFields(
                                        fieldWithPath("content").description("댓글 내용")
                                ),
                                responseFields(
                                        fieldWithPath("response").description("응답내용"),
                                        fieldWithPath("response.id").description("댓글 아이디"),
                                        fieldWithPath("response.username").description("댓글 작성자 아이디"),
                                        fieldWithPath("response.content").description("댓글 내용"),
                                        fieldWithPath("response.postKind").description("해당 댓글이 있는 게시물의 카테고리"),
                                        fieldWithPath("response.message").description("응답 메세지"),
                                        fieldWithPath("status").description("상태코드")
                                )
                        )
                );
    }



    @DisplayName("댓글 삭제")
    @WithMember("swchoi1997")
    @Test
    public void deleteComment() throws Exception {
        Long postId = createPost();
        Long commentId = createPComment(memberRepository.findByUsername("swchoi1997").orElseThrow(), postId);

        mockMvc.perform(RestDocumentationRequestBuilders.delete(BASE_URL + "/{postType}", "notice")
                        .header("Post-Id", postId)
                        .header("Comment-Id", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(document("comment/comment-delete",
                                requestHeaders(
                                        headerWithName("Post-Id").description("해당 댓글이 존재하는 게시글 아이디"),
                                        headerWithName("Comment-Id").description("해당 댓글 아이디")
                                ),
                                pathParameters(
                                        parameterWithName("postType").description("해당 댓글이 존재하는 게시글의 카테고리")
                                )

                        )
                );
    }
}