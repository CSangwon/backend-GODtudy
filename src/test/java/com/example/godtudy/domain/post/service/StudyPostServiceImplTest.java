package com.example.godtudy.domain.post.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.comment.service.CommentService;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostResponseDto;
import com.example.godtudy.domain.post.entity.PostEnum;
import com.example.godtudy.domain.post.entity.StudyPost;
import com.example.godtudy.domain.post.repository.StudyPostRepository;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import com.example.godtudy.domain.study.service.StudyService;
import com.example.godtudy.global.advice.exception.StudyNotFoundException;
import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.FileRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudyPostServiceImplTest {

    @Autowired MemberService memberService;

    @Autowired StudyPostService studyPostService;

    @Autowired CommentService commentService;

    @Autowired StudyService studyService;

    @Autowired MemberRepository memberRepository;

    @Autowired StudyPostRepository studyPostRepository;

    @Autowired StudyRepository studyRepository;

    @Autowired CommentRepository commentRepository;

    @Autowired FileRepository fileRepository;


    private MockMultipartFile getMockUploadFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/file/GODtudy_logo_2.png");
        return new MockMultipartFile("file", "file.png", "image/png",
                new FileInputStream(resource.getFile().getAbsolutePath()));
    }

    private void deleteFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        file.delete();
    }

    @BeforeEach
    void before(){
        signMember();
        createStudy();
    }

    /*
   TODO
    # 게시글 작성 성공(파일 유 / 무) --------------------------------------(v / v)
    게시글 작성 실패(권한 없음 ,이름 내용 없음, 스터디가 존재하지 않음) ------------(v / v / v)
    게시글 수정 성공(파일 유무) -------------------------------------------(v / v / v / v)
    게시글 수정 실패(권한 없음, 이름 내용없음, 스터디가 존재하지 않음)--------------(v / v / v)
    게시글 삭제(파일 유무) -----------------------------------------------(v / v)
    게시글 삭제(권한없음) ------------------------------------------------(v)
    게시글 조회 1개 ----------------------------------------------------()
    게시글 조회 조건없음 -------------------------------------------------()
    게시글 검색 제목일치 -------------------------------------------------()
    게시글 검색 내용일치 -------------------------------------------------()
    게시글 검색 제목 & 내용 일치 ------------------------------------------()

    */

    @DisplayName("게시글 작성 성공 - 파일 없음")
    @Test
    public void createStudyPostSuccess() throws Exception{
        //given
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().title("test123").content("test321").build();
        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();
//
//        Study test = studyRepository.findByUrl("test");

        //when
        PostResponseDto post = studyPostService.createPost(member, "homework", "test", postSaveRequestDto);
        StudyPost studyPost = studyPostRepository.findByTitle(post.getTitle()).orElseThrow();
        //then
        assertThat(studyPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(studyPost.getContent()).isEqualTo(post.getContent());
        assertThat(studyPost.getMember().getUsername()).isEqualTo(post.getAuthor());
        assertThat(studyPost.getFiles()).isEmpty();
        assertThat(post.getFiles()).isEmpty();
        assertThat(studyPost.getPostEnum().toString()).isEqualTo("STUDY_HOMEWORK");
        assertThat(studyPost.getStudy()).isEqualTo(studyRepository.findByUrl("test"));
        assertThat(studyPost.getCommentList()).isEmpty();
        assertThat(studyPost.getStudy().getStudyPosts().size()).isSameAs(1);

    }

    @DisplayName("게시글 작성 성공 - 파일 있음")
    @Test
    public void createStudyPostSuccessFileExist() throws Exception{
        //given
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().title("test123").content("test321").build();
        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        //when
        PostResponseDto post = studyPostService.createPost(member, "homework", "test", multipartFiles, postSaveRequestDto);
        StudyPost studyPost = studyPostRepository.findByTitle(post.getTitle()).orElseThrow();

        //then
        assertThat(studyPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(studyPost.getContent()).isEqualTo(post.getContent());
        assertThat(studyPost.getMember().getUsername()).isEqualTo(post.getAuthor());
        assertThat(studyPost.getFiles().size()).isSameAs(1);
        assertThat(post.getFiles().size()).isSameAs(1);
        assertThat(studyPost.getPostEnum().toString()).isEqualTo("STUDY_HOMEWORK");
        assertThat(studyPost.getStudy()).isEqualTo(studyRepository.findByUrl("test"));
        assertThat(studyPost.getCommentList()).isEmpty();
        assertThat(studyPost.getStudy().getStudyPosts().size()).isSameAs(1);

        File file = fileRepository.findById(studyPost.getFiles().get(0).getId()).orElseThrow();
        deleteFile(file.getFilePath());
    }

    @WithMember("swchoi1997")
    @DisplayName("게시글 작성 실패 - 권한 없음")
    @Test
    public void createStudyPostFailNotAuth() throws Exception{
        //given
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().title("test123").content("test321").build();
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(AccessDeniedException.class, () ->
                studyPostService.createPost(member, "homework", "test", postSaveRequestDto));
    }

    @DisplayName("게시글 작성 실패 - 스터디가 존재하지 않음")
    @Test
    public void createStudyPostFailNotExistStudy() throws Exception{
        //given
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().title("test123").content("test321").build();
        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(StudyNotFoundException.class, () ->
                studyPostService.createPost(member, "homework", "test1", postSaveRequestDto));
    }

    @DisplayName("게시글 작성 실패 - 제목 컨텐츠 중 하나가 없음")
    @Test
    public void createStudyPostFailNotContainTitleOrContent() throws Exception{
        //given
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().content("test321").build();
        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                studyPostService.createPost(member, "homework", "test", postSaveRequestDto));
    }

    @DisplayName("게시글 수정 성공 파일 없음 - 없음")
    @Test
    public void updateStudyPostSuccessNotFile() throws Exception{
        //given
        Member member = createStudyPostNoFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test321").content("test123").build();

        //when
        PostResponseDto post = studyPostService.updatePost(member, studyPost.getPostEnum().toString(),
                studyPost.getStudy().getUrl(), studyPost.getId(), postUpdateRequestDto);

        //then
        assertThat(studyPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(studyPost.getContent()).isEqualTo(post.getContent());
        assertThat(studyPost.getMember().getUsername()).isEqualTo(post.getAuthor());
        assertThat(studyPost.getFiles().size()).isSameAs(0);
        assertThat(post.getFiles().size()).isSameAs(0);
        assertThat(studyPost.getPostEnum().toString()).isEqualTo("STUDY_HOMEWORK");
        assertThat(studyPost.getStudy()).isEqualTo(studyRepository.findByUrl("test"));
        assertThat(studyPost.getCommentList()).isEmpty();
        assertThat(studyPost.getStudy().getStudyPosts().size()).isSameAs(1);
    }


    @DisplayName("게시글 수정 성공 파일 없음 - 있음")
    @Test
    public void updateStudyPostSuccessAddFile() throws Exception{
        //given
        Member member = createStudyPostNoFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test321").content("test123").build();

        //when
        PostResponseDto post = studyPostService.updatePost(member, studyPost.getPostEnum().toString(),
                studyPost.getStudy().getUrl(), multipartFiles, studyPost.getId(), postUpdateRequestDto);

        //then
        assertThat(studyPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(studyPost.getContent()).isEqualTo(post.getContent());
        assertThat(studyPost.getMember().getUsername()).isEqualTo(post.getAuthor());
        assertThat(studyPost.getFiles().size()).isSameAs(1);
        assertThat(post.getFiles().size()).isSameAs(1);
        assertThat(studyPost.getPostEnum().toString()).isEqualTo("STUDY_HOMEWORK");
        assertThat(studyPost.getStudy()).isEqualTo(studyRepository.findByUrl("test"));
        assertThat(studyPost.getCommentList()).isEmpty();
        assertThat(studyPost.getStudy().getStudyPosts().size()).isSameAs(1);
    }

    @DisplayName("게시글 수정 성공 파일 있음 - 없음")
    @Test
    public void updateStudyPostSuccessDeleteFile() throws Exception{
        //given
        Member member = createStudyPostFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test321").content("test123").build();

        //when
        PostResponseDto post = studyPostService.updatePost(member, studyPost.getPostEnum().toString(),
                studyPost.getStudy().getUrl(), studyPost.getId(), postUpdateRequestDto);

        //then
        assertThat(studyPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(studyPost.getContent()).isEqualTo(post.getContent());
        assertThat(studyPost.getMember().getUsername()).isEqualTo(post.getAuthor());
        assertThat(studyPost.getFiles().size()).isSameAs(0);
        assertThat(post.getFiles().size()).isSameAs(0);
        assertThat(studyPost.getPostEnum().toString()).isEqualTo("STUDY_HOMEWORK");
        assertThat(studyPost.getStudy()).isEqualTo(studyRepository.findByUrl("test"));
        assertThat(studyPost.getCommentList()).isEmpty();
        assertThat(studyPost.getStudy().getStudyPosts().size()).isSameAs(1);
    }

    @DisplayName("게시글 수정 성공 파일 있음 - 있음")
    @Test
    public void updateStudyPostSuccessExistFile() throws Exception{
        //given
        Member member = createStudyPostFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test321").content("test123").build();

        //when
        PostResponseDto post = studyPostService.updatePost(member, studyPost.getPostEnum().toString(),
                studyPost.getStudy().getUrl(), multipartFiles, studyPost.getId(), postUpdateRequestDto);

        //then
        assertThat(studyPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(studyPost.getContent()).isEqualTo(post.getContent());
        assertThat(studyPost.getMember().getUsername()).isEqualTo(post.getAuthor());
        assertThat(studyPost.getFiles().size()).isSameAs(1);
        assertThat(post.getFiles().size()).isSameAs(1);
        assertThat(studyPost.getPostEnum().toString()).isEqualTo("STUDY_HOMEWORK");
        assertThat(studyPost.getStudy()).isEqualTo(studyRepository.findByUrl("test"));
        assertThat(studyPost.getCommentList()).isEmpty();
        assertThat(studyPost.getStudy().getStudyPosts().size()).isSameAs(1);
    }

    @WithMember("swchoi1997")
    @DisplayName("게시글 수정 실패 - 권한 없음")
    @Test
    public void updateStudyPostFailNotAuth() throws Exception{
        //given
        createStudyPostFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();
        
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test321").content("test123").build();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(AccessDeniedException.class, () ->
                studyPostService.updatePost(member, "homework", "test", studyPost.getId(), postUpdateRequestDto));
    }


//    PostResponseDto updatePost(Member member, String post, String studyUrl, Long id, PostUpdateRequestDto postUpdateRequestDto);
    @DisplayName("게시글 수정 실패 - 스터디가 존재하지 않음")
    @Test
    public void updateStudyPostFailNotExistStudy() throws Exception{
        //given
        Member member = createStudyPostFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test321").content("test123").build();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(StudyNotFoundException.class, () ->
                studyPostService.updatePost(member, "homework", "test1", studyPost.getId(), postUpdateRequestDto));
    }

    @DisplayName("게시글 수정 실패 - 제목 컨텐츠 중 하나가 없음")
    @Test
    public void updateStudyPostFailNotContainTitleOrContent() throws Exception{
        //given
        Member member = createStudyPostFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().content("test123").build();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                studyPostService.updatePost(member, "homework", "test", studyPost.getId(), postUpdateRequestDto));
    }

    @DisplayName("게시글 삭제 - 파일 없음")
    @Test
    void deleteStudyPostNotExistFile() throws Exception {
        //given
        Member member = createStudyPostNoFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        //when
        studyPostService.deletePost(member, studyPost.getPostEnum().toString(), studyPost.getStudy().getUrl(), studyPost.getId());

        //then
        assertThat(studyPostRepository.findByTitle("test123")).isEmpty();
        assertThat(studyPostRepository.findAll().size()).isSameAs(0);
        assertThat(member.getStudyPosts().size()).isSameAs(0);
    }

    @DisplayName("게시글 삭제 - 파일 있음")
    @Test
    void deleteStudyPostExistFile() throws Exception {
        //given
        Member member = createStudyPostFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        //when
        studyPostService.deletePost(member, studyPost.getPostEnum().toString(), studyPost.getStudy().getUrl(), studyPost.getId());

        //then
        assertThat(studyPostRepository.findByTitle("test123")).isEmpty();
        assertThat(studyPostRepository.findAll().size()).isSameAs(0);
        assertThat(member.getStudyPosts().size()).isSameAs(0);
        assertThat(fileRepository.findAll().size()).isSameAs(0);
    }

    @WithMember("swchoi1997")
    @DisplayName("게시글 삭제 실패- 권한없음")
    @Test
    void deleteStudyPostFailNotAuth() throws Exception {
        //given
        createStudyPostNoFile();
        StudyPost studyPost = studyPostRepository.findByTitle("test123").orElseThrow();

        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();

        //when //then
        org.junit.jupiter.api.Assertions.assertThrows(AccessDeniedException.class, () ->
                studyPostService.deletePost(member, studyPost.getPostEnum().toString(),
                        studyPost.getStudy().getUrl(), studyPost.getId()));
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

    private void createStudy(){
        Member teacher = memberRepository.findByUsername("test1").orElseThrow();
        teacher.setRole(Role.TEACHER);
        Member student = memberRepository.findByUsername("swchoi123").orElseThrow();
        student.setRole(Role.STUDENT);

        CreateStudyRequestDto createStudyRequestDto = CreateStudyRequestDto
                .builder().name("test").teacherId(teacher.getId())
                .studentId(student.getId()).url("test").subject("ENGLISH")
                .shortDescription("test123").build();

        studyService.createStudyByTeacher(teacher, createStudyRequestDto);
    }

    private Member createStudyPostNoFile(){
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().title("test123").content("test321").build();
        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();
        studyPostService.createPost(member, "homework", "test",postSaveRequestDto);
        return member;
    }

    private Member createStudyPostFile() throws IOException {
        PostSaveRequestDto postSaveRequestDto =  PostSaveRequestDto.builder().title("test123").content("test321").build();
        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());
        studyPostService.createPost(member, "homework", "test", multipartFiles, postSaveRequestDto);

        return member;
    }

}