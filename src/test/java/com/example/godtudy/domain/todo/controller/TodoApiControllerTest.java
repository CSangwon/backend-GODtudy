package com.example.godtudy.domain.todo.controller;

import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.todo.dto.request.CreateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.request.UpdateTodoRequestDto;
import com.example.godtudy.domain.todo.dto.response.TodoDto;
import com.example.godtudy.domain.todo.dto.response.TodoPagingDto;
import com.example.godtudy.domain.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class TodoApiControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void createTodo() throws Exception {
        //given
        TodoDto todoDto = TodoDto.builder()
                .id(1L)
                .title("todo title")
                .content("todo content")
                .endDate(LocalDateTime.of(2020, 2, 12, 0, 0, 0))
                .studyUrl("study Url")
                .build();

        given(todoService.createTodo(ArgumentMatchers.anyString(), any(CreateTodoRequestDto.class)))
                .willReturn(todoDto);

        //when
        CreateTodoRequestDto createTodoRequestDto = CreateTodoRequestDto.builder()
                .title("todo title")
                .content("todo content")
                .endDate(LocalDateTime.parse("1997-02-12T00:00:00"))
                .build();

        ResultActions result = this.mockMvc.perform(post("/api/study/{studyUrl}/todo", "new-study")
                .content(objectMapper.writeValueAsString(createTodoRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("todo/todo-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyUrl").description("스터디 url")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("todo 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("todo 내용"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("마감기한")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("todo id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("todo 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("todo 내용"),
                                fieldWithPath("endDate").type(JsonFieldType.ARRAY).description("todo 마감기한"),
                                fieldWithPath("studyUrl").type(JsonFieldType.STRING).description("스터디 url")
                        )
                ));

    }

    @Test
    public void updateTodo() throws Exception {
        //given
        TodoDto todoDto = TodoDto.builder()
                .id(1L)
                .title("todo title")
                .content("todo content")
                .endDate(LocalDateTime.of(2020, 2, 12, 0, 0, 0))
                .studyUrl("study Url")
                .build();

        given(todoService.updateTodo(ArgumentMatchers.anyLong(), any(UpdateTodoRequestDto.class)))
                .willReturn(todoDto);

        //when
        UpdateTodoRequestDto updateTodoRequestDto = UpdateTodoRequestDto.builder()
                .title("todo title - 수정")
                .content("todo content - 수정")
                .endDate(LocalDateTime.parse("1997-02-12T00:00:00"))
                .build();

        ResultActions result = this.mockMvc.perform(put("/api/study/{studyUrl}/todo/{todoId}", "new-study", "1")
                .content(objectMapper.writeValueAsString(updateTodoRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("todo/todo-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyUrl").description("스터디 url"),
                                parameterWithName("todoId").description("todo id")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("todo 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("todo 내용"),
                                fieldWithPath("endDate").type(JsonFieldType.STRING).description("마감기한")
                                ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("todo id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("todo 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("todo 내용"),
                                fieldWithPath("endDate").type(JsonFieldType.ARRAY).description("todo 마감기한"),
                                fieldWithPath("studyUrl").type(JsonFieldType.STRING).description("스터디 url")
                        )
                ));

    }

    @Test
    public void getTodo() throws Exception {
        //given
        TodoDto todoDto = TodoDto.builder()
                .id(1L)
                .title("todo title")
                .content("todo content")
                .endDate(LocalDateTime.of(2020, 2, 12, 0, 0, 0))
                .studyUrl("study Url")
                .build();

        given(todoService.getTodo(ArgumentMatchers.anyLong()))
                .willReturn(todoDto);

        //when
        ResultActions result = this.mockMvc.perform(get("/api/study/{studyUrl}/todo/{todoId}", "new-study", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then

        result.andExpect(status().isOk())
                .andDo(document("todo/todo-get-one",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyUrl").description("스터디 url"),
                                parameterWithName("todoId").description("todo id")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("todo id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("todo 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("todo 내용"),
                                fieldWithPath("endDate").type(JsonFieldType.ARRAY).description("todo 마감기한"),
                                fieldWithPath("studyUrl").type(JsonFieldType.STRING).description("스터디 url")
                        )
                ));

    }

    @Test
    public void deleteTodo() throws Exception {
        //given
        Long id = 1L;
        given(todoService.deleteTodo(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
                .willReturn(id);
        //when
        ResultActions result = this.mockMvc.perform(delete("/api/study/{studyUrl}/todo", "new-study")
                .header("Todo-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("todo/todo-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Todo-Id").description("todo id")
                        )
                ));

    }

    @Test
    public void getTodos() throws Exception {
        //given
        TodoDto todoDto = TodoDto.builder()
                .id(1L)
                .title("todo title")
                .content("todo content")
                .endDate(LocalDateTime.of(2020, 2, 12, 0, 0, 0))
                .studyUrl("study Url")
                .build();

        List<TodoDto> todoDtoList = new ArrayList<>();
        todoDtoList.add(todoDto);

        Pageable pageable = PageRequest.of(0, 10);

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), todoDtoList.size());
        Page<TodoDto> todoDtoPage = new PageImpl<>(todoDtoList.subList(start, end), pageable, todoDtoList.size());

        TodoPagingDto todoPagingDto = new TodoPagingDto(todoDtoPage);

        given(todoService.getTodoList(ArgumentMatchers.anyString(), ArgumentMatchers.any(PageRequest.class)))
                .willReturn(todoPagingDto);
        
        //when
        ResultActions result = this.mockMvc.perform(get("/api/study/{studyUrl}/todo", "new-study")
//                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        //then
        result.andExpect(status().isOk())
                .andDo(document("todo/todo-get-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("studyUrl").description("스터디 url")
                        ),
                        responseFields(
                                fieldWithPath("totalPageCount").type(JsonFieldType.NUMBER).description("전체 page의 수"),
                                fieldWithPath("currentPageNumber").type(JsonFieldType.NUMBER).description("현재 page의 수"),
                                fieldWithPath("totalElementCount").type(JsonFieldType.NUMBER).description("전체 content의 개수"),
                                fieldWithPath("currentPageElementCount").type(JsonFieldType.NUMBER).description("한 페이지당 content의 개수"),
                                fieldWithPath("todoDtoList.[].id").type(JsonFieldType.NUMBER).description("todo id"),
                                fieldWithPath("todoDtoList.[].title").type(JsonFieldType.STRING).description("todo 제목"),
                                fieldWithPath("todoDtoList.[].content").type(JsonFieldType.STRING).description("todo 내용"),
                                fieldWithPath("todoDtoList.[].endDate").type(JsonFieldType.ARRAY).description("todo 마감기한"),
                                fieldWithPath("todoDtoList.[].studyUrl").type(JsonFieldType.STRING).description("스터디 url")
                        )
                ));

    }


}