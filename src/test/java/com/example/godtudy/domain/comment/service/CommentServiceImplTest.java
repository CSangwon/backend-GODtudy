package com.example.godtudy.domain.comment.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.comment.dto.CommentSaveDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.AdminPostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.example.godtudy.domain.post.service.AdminPostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceImplTest {

    @Autowired AdminPostRepository adminPostRepository;
    @Autowired AdminPostService adminPostService;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentService commentService;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;


    // 맴버2 가입시키기
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

    @AfterEach
    void deletePostAndMember() {
        memberRepository.deleteAll();
    }

    // 게시글 작성하기 파일 없게
    private Long savePostNotice(Member member) {
        AdminPost adminPost = AdminPost.builder().title("test").content("tes123")
                .files(new ArrayList<>()).commentList(new ArrayList<>())
                .member(member).noticeOrEvent(AdminPostEnum.NOTICE).build();
        return adminPostRepository.save(adminPost).getId();
    }

    // 댓글달기
    private Long saveComment(Member member, AdminPost adminPost) {
        Comment comment = Comment.builder().content("test1").isFirstComment(true).writer(member).adminPost(adminPost).build();
        return commentRepository.save(comment).getId();
    }

    // 대댓글 달기
    private Long saveReComment(Member member,AdminPost adminPost, Long parentId) {
        Comment parent = commentRepository.findById(parentId).orElseThrow();
        Comment child = Comment.builder().content("child").writer(member).adminPost(adminPost).parentComment(parent).build();

        return commentRepository.save(child).getId();
    }

    @Test
    @DisplayName("첫번째 댓글 저장 - 성공")
    @WithMember("swchoi1997")
    public void saveFisrtCommentSuccess() throws Exception{
        //given 게시글 작성 -> 댓글작성
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Member member2 = memberRepository.findByUsername("swchoi123").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        CommentSaveDto commentSaveDto2 = CommentSaveDto.builder().content("test2").build();

        //when
        commentService.saveComment(postId, member, commentSaveDto);
        commentService.saveComment(postId, member2, commentSaveDto2);
        Comment comment = commentRepository.findByContent("test1").orElseThrow();
        Comment comment2 = commentRepository.findByContent("test2").orElseThrow();

        //then

        assertThat(comment.getContent()).isEqualTo("test1");
        assertThat(comment.getWriter()).isEqualTo(member);
        assertThat(comment.getAdminPost()).isEqualTo(adminPostRepository.findById(postId).orElseThrow());
        assertThat(comment.getParentComment()).isNull();
        assertThat(comment.getChildComment()).isEmpty();

        assertThat(comment2.getContent()).isEqualTo("test2");
        assertThat(comment2.getWriter()).isEqualTo(member2);
        assertThat(comment2.getAdminPost()).isEqualTo(adminPostRepository.findById(postId).orElseThrow());
        assertThat(comment2.getParentComment()).isNull();
        assertThat(comment2.getChildComment()).isEmpty();
    }

    @Test
    @WithMember("swchoi1997")
    @DisplayName("대댓글 저장 성공")
    public void saveReCommentSuccess() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment(postId, member, commentSaveDto);
        CommentSaveDto reCommentSaveDto = CommentSaveDto.builder().content("test2").build();

        //when
        Comment parentComment = commentRepository.findByContent("test1").orElseThrow();
        commentService.saveReComment(postId, member, parentComment.getId(), reCommentSaveDto);
        Comment reComment = commentRepository.findByContent("test2").orElseThrow();

        //then
        assertThat(parentComment.getChildComment().get(0)).isEqualTo(reComment);
        assertThat(parentComment.getChildComment()).isNotNull();
        assertThat(parentComment.getChildComment().get(0).getContent()).isEqualTo("test2");
        assertThat(reComment.getParentComment()).isEqualTo(parentComment);
        assertThat(reComment.getWriter()).isEqualTo(member);
        assertThat(reComment.getContent()).isEqualTo("test2");
        assertThat(reComment.getParentComment().getContent()).isEqualTo("test1");

    }



//    @Test
//    @WithMember("swchoi1997")
//    public void test() throws Exception{
//        System.out.println("-------------------------------------------");
//        Member member = memberRepository.findByUsername("swchoi123").orElseThrow();
//        System.out.println("member = " + member.getUsername());
//        Member member1 = memberRepository.findByUsername("swchoi1997").orElseThrow();
//        System.out.println("member1.getUsername() = " + member1.getUsername());
//
//        System.out.println("-------------------------------------------");
//        Long swchoi123Post = savePostNotice(member);
//        AdminPost adminPost = adminPostRepository.findById(swchoi123Post).orElseThrow();
//        System.out.println("adminPost.getTitle() = " + adminPost.getTitle() + " " + adminPost.getMember().getUsername());
//        Long swchoi1997Post = savePostNotice(member1);
//        AdminPost adminPost2 = adminPostRepository.findById(swchoi1997Post).orElseThrow();
//        System.out.println("adminPost.getTitle() = " + adminPost2.getTitle() + " " + adminPost2.getMember().getUsername());
//
//        System.out.println("-------------------------------------------");
//        Long comment1swchoi123 = saveComment(member, adminPost);
//        Comment comment = commentRepository.findById(comment1swchoi123).orElseThrow();
//        System.out.println("comment = " + comment.getContent() + " " + comment.getWriter().getUsername());
//        Long reComment1swchoi123 = saveReComment(member, adminPost, comment1swchoi123);
//        Comment comment1 = commentRepository.findById(reComment1swchoi123).orElseThrow();
//        System.out.println("comment = " + comment1.getContent() + " " + comment1.getWriter().getUsername());
//

//    }

}