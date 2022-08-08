package com.example.godtudy.domain.post.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.comment.dto.CommentSaveDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.comment.service.CommentService;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.AdminPostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;

import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.FileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class AdminPostServiceTest {

    @Autowired    MemberService memberService;

    @Qualifier("adminPostService")
    @Autowired    PostService postService;

    @Autowired    CommentService commentService;

    @Autowired    MemberRepository memberRepository;

    @Autowired    AdminPostRepository adminPostRepository;

    @Autowired    CommentRepository commentRepository;

    @Autowired    FileRepository fileRepository;




    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.png", "image/png",
                new FileInputStream("/Users/forest_choi/Desktop/portfolio/GODtudy/Godtudy_File/test1.png"));
    }

    private void deleteFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        file.delete();
    }

    @BeforeEach
    void signMember2(){
        List<SubjectEnum> subjectEnums = new ArrayList<>();
        subjectEnums.add(SubjectEnum.BIOLOGY);
        subjectEnums.add(SubjectEnum.CHEMISTRY);
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



    @Test
    @DisplayName("게시글 생성 성공 공지사항 - 파일 없음")
    @WithMember("swchoi1997")
    public void postCreateSuccessNotFile(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        //when
        postService.createPost(member, post, postSaveRequestDto);

        //then
        AdminPost test1 = adminPostRepository.findByTitle("test1").orElseThrow();

        assertThat(test1).isNotNull();
        assertThat(test1.getTitle()).isEqualTo("test1");
        assertThat(test1.getContent()).isEqualTo("test1");
        assertThat(test1.getMember().getUsername()).isEqualTo("swchoi1997");
        assertThat(test1.getFiles().isEmpty()).isTrue();
        assertThat(test1.getNoticeOrEvent()).isEqualTo(AdminPostEnum.valueOf(post.toUpperCase()));
    }


    @Test
    @DisplayName("게시글 생성 성공 공지사항 - 파일 있음")
    @WithMember("swchoi1997")
    public void postCreateSuccessExistFile() throws IOException{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        //when
        postService.createPost(member, multipartFiles, post, postSaveRequestDto);

        //then
        AdminPost test1 = adminPostRepository.findByTitle("test1").orElseThrow();

        System.out.println(test1.getFiles().get(1));

        assertThat(test1).isNotNull();
        assertThat(test1.getTitle()).isEqualTo("test1");
        assertThat(test1.getContent()).isEqualTo("test1");
        assertThat(test1.getMember().getUsername()).isEqualTo("swchoi1997");
        assertThat(test1.getNoticeOrEvent()).isEqualTo(AdminPostEnum.valueOf(post.toUpperCase()));


        File file = fileRepository.findById(test1.getFiles().get(0).getId()).orElseThrow();
        assertThat(test1.getFiles()).isNotEmpty();
        assertThat(file.getFilePath()).isEqualTo(test1.getFiles().get(0).getFilePath());

        deleteFile(file.getFilePath());
    }

    @Test
    @DisplayName("게시글 생성 실패 공지사항 - 권한 없음")
    @WithMember("swchoi1997")
    public void postCreateFailByNotAccess(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        member.setRole(Role.STUDENT);
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();


        //when //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                postService.createPost(member, post, postSaveRequestDto));
    }

    @Test
    @DisplayName("게시글 생성 실패 - 이름, 내용 없음")
    @WithMember("swchoi1997")
    public void postCreateFailNotExistPostNameOrContent(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto dtoNotContainTitle = PostSaveRequestDto.builder().content("test1").build();
        PostSaveRequestDto dtoNotContainContent = PostSaveRequestDto.builder().title("test1").build();

        //when //then
        Assertions.assertThrows(Exception.class, () ->
                postService.createPost(member, post, dtoNotContainTitle));
        Assertions.assertThrows(Exception.class, () ->
                postService.createPost(member, post, dtoNotContainContent));
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 없음 -> 없음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessNotExistFile(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);

        //일단 게시물이 하나밖에 없다고 가정하고 title로 가져올 것 => id를 어떻게 가져와야할지 모르겠음 EntityManager사용해야하나...?
        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        postService.updatePost(member, post, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test2").orElseThrow();
        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test2");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());
        assertThat(updateAdminPost.getFiles()).isEmpty();
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 없음 -> 있음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessAddFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test1").content("test2").build();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        postService.updatePost(member, post, multipartFiles, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test1");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());

        File file = fileRepository.findById(updateAdminPost.getFiles().get(0).getId()).orElseThrow();
        assertThat(updateAdminPost.getFiles()).isNotEmpty();
        assertThat(file.getFilePath()).isEqualTo(updateAdminPost.getFiles().get(0).getFilePath());

        deleteFile(file.getFilePath());
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 있음 -> 없음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessMinusFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        postService.createPost(member, multipartFiles, post, postSaveRequestDto);

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();


        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        postService.updatePost(member, post, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test2").orElseThrow();
        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test2");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());
        assertThat(updateAdminPost.getFiles()).isEmpty();
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 있음 -> 있음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessChangeFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        postService.createPost(member,multipartFiles, post, postSaveRequestDto);
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();

        String initFilePath = adminPost.getFiles().get(0).getFilePath();

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();
        List<MultipartFile> multipartFiles2 = new ArrayList<>();
        multipartFiles2.add(getMockUploadFile());

        postService.updatePost(member, post, multipartFiles2, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test2").orElseThrow();

        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test2");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());

        File file = fileRepository.findById(updateAdminPost.getFiles().get(0).getId()).orElseThrow();

        assertThat(updateAdminPost.getFiles()).isNotEmpty();
        assertThat(file.getFilePath()).isNotEqualTo(initFilePath);

        deleteFile(file.getFilePath());
    }

    @Test
    @DisplayName("게시글 수정 실패 공지사항 - 권한 없음")
    @WithMember("swchoi1997")
    public void postUpdateFailByNotAccess(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();
        member.setRole(Role.STUDENT);

        //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                postService.updatePost(member, post, adminPost.getId(), postUpdateRequestDto));
    }

    @Test
    @DisplayName("게시글 수정 실패 공지사항 - 작성자 아님")
    @WithMember("swchoi1997")
    public void postUpdateFailByNotAuthor(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();

        Member testMember = tmp_member();

        //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                postService.updatePost(testMember, post, adminPost.getId(), postUpdateRequestDto));
    }

    private Member tmp_member() {
        List<SubjectEnum> subjectEnums = new ArrayList<>();
        subjectEnums.add(SubjectEnum.BIOLOGY);
        subjectEnums.add(SubjectEnum.CHEMISTRY);
        MemberJoinForm memberJoinForm = MemberJoinForm.builder()
                .username("test123")
                .password("tkddnjs4371@")
                .name("유하연")
                .email("test123@naver.com")
                .nickname("test123")
                .year("1997").month("02").day("12")
                .subject(subjectEnums)
                .build();
        return memberService.initJoinMember(memberJoinForm, "admin");
    }

    @Test
    @DisplayName("게시글 수정 실패 공지사항 - 원래 게시물 아님")
    @WithMember("swchoi1997")
    public void postUpdateFailByNotCurrCategory(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();
        String newPost = "event";

        //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                postService.updatePost(member, newPost, adminPost.getId(), postUpdateRequestDto));
    }

    @Test
    @DisplayName("게시물 삭제 - 성공, 파일 없었음")
    @WithMember("swchoi1997")
    public void postDeleteSuccess(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);

        assertThat(adminPostRepository.findAll().size()).isSameAs(11);
        //when
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        postService.deletePost(member, adminPost.getId());

        //then
        assertThat(adminPostRepository.findAll().size()).isSameAs(10);
//        assertThat(member.getAdminPosts().size()).isSameAs(0);
    }

    @Test
    @DisplayName("게시물 삭제 - 성공, 파일 있었음")
    @WithMember("swchoi1997")
    public void postDeleteSuccessExistedFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());
        postService.createPost(member,multipartFiles, post, postSaveRequestDto);

        assertThat(adminPostRepository.findAll().size()).isSameAs(11);
        //when
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        postService.deletePost(member, adminPost.getId());

        //then
        assertThat(adminPostRepository.findAll().size()).isSameAs(10);
        assertThat(fileRepository.findAll().size()).isSameAs(0);
    }

    @Test
    @DisplayName("게시물 삭제 - 실패, 권한없음")
    @WithMember("swchoi1997")
    public void postDeleteFailNotAdmin(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);

        //when
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        member.setRole(Role.STUDENT);

        //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                postService.deletePost(member, adminPost.getId()));
    }

    @Test
    @DisplayName("게시물 삭제 - 실패, 작성자 아님")
    @WithMember("swchoi1997")
    public void postDeleteFailNotAuthor(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        postService.createPost(member, post, postSaveRequestDto);

        //when
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        Member tmpMember = tmp_member();

        //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                postService.deletePost(tmpMember, adminPost.getId()));
    }



    //TODO 현재 짜야하는 테스트코드
    /*
    1. memberService에서 맴버 삭제 시 게시글, 댓글 사라지게 해야함
    2. postService에서 post삭제 시 댓글 삭제 해줘야함
    3. 게시글 조회 시 댓글까지 조회되는지 확인 : 댓글의 대댓글까지 조화가 되야함!!
     */
    @Test
    @DisplayName("삭제시 댓글도 같이 삭제")
    @WithMember("swchoi1997")
    public void deletePostCheckMemberAndComment() throws Exception{
        //given
        Member member2 = memberRepository.findByUsername("swchoi1997").orElseThrow();
        member2.setRole(Role.ADMIN);

        PostSaveRequestDto postSaveRequestDto2 = PostSaveRequestDto.builder().title("test2").content("content2").build();
        postService.createPost(member2, "event", postSaveRequestDto2);
        AdminPost adminPost2 = adminPostRepository.findByTitle("test2").orElseThrow();

        CommentSaveDto commentSaveDto2 = CommentSaveDto.builder().content("comment2").build();
        commentService.saveComment(adminPost2.getId(), member2, commentSaveDto2);
        Comment comment2 = commentRepository.findByContent("comment2").orElseThrow();

        CommentSaveDto reCommentSaveDto2 = CommentSaveDto.builder().content("comment4").build();
        commentService.saveReComment(adminPost2.getId(), member2, comment2.getId(), reCommentSaveDto2);

        assertThat(commentRepository.findAll().size()).isSameAs(112);
        //when
//        adminPost2 = adminPostRepository.findByTitle("test2").orElseThrow();
        postService.deletePost(member2, adminPost2.getId());

        //then
        assertThat(commentRepository.findAll().size()).isSameAs(110);
        assertThat(adminPostRepository.findAll().size()).isSameAs(10);
        assertThat(member2.getAdminPosts().size()).isSameAs(0);
    }


    @Test
    @DisplayName("게시물 조회 1개")
    @WithMember("swchoi1997")
    void postFindJustOne(){
        //given
        Member member1 = memberRepository.findByUsername("swchoi123").orElseThrow();
        Member member2 = memberRepository.findByUsername("swchoi1997").orElseThrow();
        member1.setRole(Role.ADMIN);
        member2.setRole(Role.ADMIN);

        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("content1").build();
        PostSaveRequestDto postSaveRequestDto2 = PostSaveRequestDto.builder().title("test2").content("content2").build();
        postService.createPost(member1, "notice", postSaveRequestDto);
        postService.createPost(member2, "event", postSaveRequestDto2);
        AdminPost adminPost1 = adminPostRepository.findByTitle("test1").orElseThrow();
        AdminPost adminPost2 = adminPostRepository.findByTitle("test2").orElseThrow();

        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("comment1").build();
        CommentSaveDto commentSaveDto2 = CommentSaveDto.builder().content("comment2").build();
        commentService.saveComment(adminPost1.getId(), member1, commentSaveDto);
        commentService.saveComment(adminPost2.getId(), member2, commentSaveDto2);
        Comment comment = commentRepository.findByContent("comment1").orElseThrow();
        Comment comment2 = commentRepository.findByContent("comment2").orElseThrow();

        CommentSaveDto reCommentSaveDto = CommentSaveDto.builder().content("comment3").build();
        CommentSaveDto reCommentSaveDto2 = CommentSaveDto.builder().content("comment4").build();
        commentService.saveReComment(adminPost1.getId(), member2, comment.getId(), reCommentSaveDto);
        commentService.saveReComment(adminPost2.getId(), member1, comment2.getId(), reCommentSaveDto2);

        //when
        adminPost1 = adminPostRepository.findByTitle("test1").orElseThrow();
        adminPost2 = adminPostRepository.findByTitle("test2").orElseThrow();
        PostInfoResponseDto postInfo1 = postService.getPostInfo(adminPost1.getId());
        PostInfoResponseDto postInfo2= postService.getPostInfo(adminPost2.getId());

        //then
        assertThat(postInfo1.getTitle()).isEqualTo("test1");
        assertThat(postInfo2.getTitle()).isEqualTo("test2");
        assertThat(postInfo1.getContent()).isEqualTo("content1");
        assertThat(postInfo2.getContent()).isEqualTo("content2");
        assertThat(postInfo1.getAuthor()).isEqualTo("숲속");
        assertThat(postInfo2.getAuthor()).isEqualTo("숲속의냉면");

        assertThat(postInfo1.getCommentInfoResponseDtoList().size()).isSameAs(1);
        assertThat(postInfo2.getCommentInfoResponseDtoList().size()).isSameAs(1);
        assertThat(postInfo1.getCommentInfoResponseDtoList().get(0).getPostId()).isEqualTo(postInfo1.getPostId());
        assertThat(postInfo2.getCommentInfoResponseDtoList().get(0).getPostId()).isEqualTo(postInfo2.getPostId());
        assertThat(postInfo1.getCommentInfoResponseDtoList().get(0).getContent()).isEqualTo("comment1");
        assertThat(postInfo2.getCommentInfoResponseDtoList().get(0).getContent()).isEqualTo("comment2");
        assertThat(postInfo1.getCommentInfoResponseDtoList().get(0).getReCommentInfoResponseDtoList().get(0).getContent()).isEqualTo("comment3");
        assertThat(postInfo2.getCommentInfoResponseDtoList().get(0).getReCommentInfoResponseDtoList().get(0).getContent()).isEqualTo("comment4");
    }

}