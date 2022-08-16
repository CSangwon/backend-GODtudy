package com.example.godtudy.domain.comment.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.comment.dto.request.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.request.CommentUpdateDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.PostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.example.godtudy.domain.post.service.AdminPostService;
import com.example.godtudy.global.advice.exception.CommentNotFoundException;
import com.example.godtudy.global.advice.exception.PostNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceImplTest {

    @Autowired AdminPostRepository adminPostRepository;
    @Autowired AdminPostService postService;
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
                .member(member).noticeOrEvent(PostEnum.NOTICE).build();
        return adminPostRepository.save(adminPost).getId();
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
        commentService.saveComment("notice", postId, member, commentSaveDto);
        commentService.saveComment("notice",postId, member2, commentSaveDto2);
        Comment comment = commentRepository.findByContent("test1").orElseThrow();
        Comment comment2 = commentRepository.findByContent("test2").orElseThrow();

        //then

        assertThat(comment.getContent()).isEqualTo("test1");
        assertThat(comment.getWriter()).isEqualTo(member);
        assertThat(comment.getAdminPost()).isEqualTo(adminPostRepository.findById(postId).orElseThrow());
        assertThat(comment.getParentComment()).isNull();
        assertThat(comment.getChildComment()).isEmpty();
        assertThat(comment.getAdminPost().getCommentList().size()).isSameAs(2);
        assertThat(comment.getStudyPost()).isNull();

        assertThat(comment2.getContent()).isEqualTo("test2");
        assertThat(comment2.getWriter()).isEqualTo(member2);
        assertThat(comment2.getAdminPost()).isEqualTo(adminPostRepository.findById(postId).orElseThrow());
        assertThat(comment2.getParentComment()).isNull();
        assertThat(comment2.getChildComment()).isEmpty();
        assertThat(comment2.getAdminPost().getCommentList().size()).isSameAs(2);
        assertThat(comment2.getStudyPost()).isNull();
    }

    @Test
    @WithMember("swchoi1997")
    @DisplayName("대댓글 저장 성공")
    public void saveReCommentSuccess() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, commentSaveDto);
        CommentSaveDto reCommentSaveDto = CommentSaveDto.builder().content("test2").build();

        //when
        Comment parentComment = commentRepository.findByContent("test1").orElseThrow();
        commentService.saveReComment("notice",postId, member, parentComment.getId(), reCommentSaveDto);
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

    @Test
    @DisplayName("댓글 작성 실패 - 게시글이 없음")
    @WithMember("swchoi1997")
    public void saveCommentFailByNotExistPost() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = 120934871234987L;
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();

        //when//then
        Assertions.assertThrows(PostNotFoundException.class, () ->
                commentService.saveComment("notice",postId, member, commentSaveDto));
    }

    @Test
    @DisplayName("대댓글 작성 실패 - 부모 댓글이 없음")
    @WithMember("swchoi1997")
    public void saveReCommentFailByNotParentComment() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto reCommentSaveDto = CommentSaveDto.builder().content("test2").build();

        //when//then
        Assertions.assertThrows(CommentNotFoundException.class, () ->
                commentService.saveReComment("notice",postId, member,123L, reCommentSaveDto));
    }

    @Test
    @DisplayName("대댓글 작성 실패 - 게시글이 없음")
    @WithMember("swchoi1997")
    public void saveReCommentFailByNotExistPost(){
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, commentSaveDto);

        CommentSaveDto reCommentSaveDto = CommentSaveDto.builder().content("test2").build();

        //when//then
        Long postId2 = 123L;
        Assertions.assertThrows(PostNotFoundException.class, () ->
                commentService.saveReComment("notice",postId2, member,123L, reCommentSaveDto));
    }


    @Test
    @WithMember("swchoi1997")
    @DisplayName("댓글 수정 성공")
    public void updateCommentSuccess() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, commentSaveDto);
        Long test1 = commentRepository.findByContent("test1").orElseThrow().getId();

        //when
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder().content("test2").build();
        commentService.updateComment("notice", test1, member, commentUpdateDto);

        //then
        Comment updateComment = commentRepository.findByContent("test2").orElseThrow();
        assertThat(updateComment.getContent()).isEqualTo("test2");
    }

    @Test
    @WithMember("swchoi1997")
    @DisplayName("댓글 수정 실패 - 권한없음(본인아님)")
    public void updateCommentFailByNotAuth() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, commentSaveDto);
        Long test1 = commentRepository.findByContent("test1").orElseThrow().getId();

        //when
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder().content("test2").build();
        Member newMember = memberRepository.findByUsername("swchoi123").orElseThrow();

        //then
        assertThrows(AccessDeniedException.class, () ->
                commentService.updateComment("notice", test1, newMember, commentUpdateDto));
    }

    @Test
    @WithMember("swchoi1997")
    @DisplayName("댓글 삭제 성공 - 첫번째 댓글 - 자식없음")
    public void deleteCommentSuccessFirstComment() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, commentSaveDto);

        Comment comment = commentRepository.findByContent("test1").orElseThrow();


        //when
        Long commentId = comment.getId();
        commentService.deleteComment("notice",commentId, member);

        //then
        assertThat(commentRepository.findAll().size()).isSameAs(110);
        assertThat(adminPostRepository.findById(postId).get().getCommentList().size()).isSameAs(0);
    }


    @Test
    @WithMember("swchoi1997")
    @DisplayName("댓글 삭제 성공 - 첫번째 댓글 - 자식있음")
    public void deleteCommentSuccessFirstCommentExistChild() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);

        CommentSaveDto parent = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, parent);
        Comment comment = commentRepository.findByContent("test1").orElseThrow();

        CommentSaveDto child = CommentSaveDto.builder().content("test2").build();
        commentService.saveReComment("notice",postId, member, comment.getId(), child);
        Comment comment1 = commentRepository.findByContent("test2").orElseThrow();


        //when
        Long commentId = comment.getId();
        commentService.deleteComment("notice",commentId, member);

        //then
        assertThat(comment.getWriter()).isNull();
        assertThat(comment.getContent()).isEqualTo("");
        assertThat(comment.getChildComment().size()).isSameAs(1);
        assertThat(comment1.getParentComment()).isNotNull();
        assertThat(comment1.getParentComment().getWriter()).isNull();
        assertThat(comment1.getParentComment().getContent()).isEqualTo("");
    }

    @Test
    @WithMember("swchoi1997")
    @DisplayName("댓글 삭제 성공 - 첫번째 댓글 - 자식없음 - 관리자 삭제")
    public void deleteCommentSuccessByAdministrator() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);
        CommentSaveDto commentSaveDto = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, commentSaveDto);

        Comment comment = commentRepository.findByContent("test1").orElseThrow();


        //when
        Long commentId = comment.getId();
        member.setRole(Role.ADMIN);
        commentService.deleteComment("notice",commentId, member);

        //then
        assertThat(commentRepository.findAll().size()).isSameAs(110);
        assertThat(adminPostRepository.findById(postId).get().getCommentList().size()).isSameAs(0);
    }

    @Test
    @WithMember("swchoi1997")
    @DisplayName("댓글 삭제 실패 - 작성자가 아님")
    public void deleteCommentFail() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        Long postId = savePostNotice(member);

        CommentSaveDto parent = CommentSaveDto.builder().content("test1").build();
        commentService.saveComment("notice",postId, member, parent);
        Comment comment = commentRepository.findByContent("test1").orElseThrow();

        Member member2 = memberRepository.findByUsername("swchoi123").orElseThrow();

        //when //then
        Long commentId = comment.getId();
        assertThrows(AccessDeniedException.class, () ->
                commentService.deleteComment("notice",commentId, member2));
    }

}