package com.example.godtudy.domain.comment.service;

import com.example.godtudy.domain.comment.dto.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.CommentUpdateDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.example.godtudy.global.advice.exception.CommentNotFoundException;
import com.example.godtudy.global.advice.exception.MemberNotFoundException;
import com.example.godtudy.global.advice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final AdminPostRepository adminPostRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


    @Override
    public ResponseEntity<?> saveComment(Long postId, Member member, CommentSaveDto commentSaveDto) {
        Comment comment = commentSaveDto.toEntity();
        checkIfUserAndNotTmpUser(member);

        comment.setWriter(member);
        comment.setAdminPost(checkPostExist(postId));
        comment.setIsFirstComment();

        commentRepository.save(comment);

        return new ResponseEntity<>("Comment save", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> saveReComment(Long postId, Member member, Long parentCommentId, CommentSaveDto commentSaveDto) {
        Comment reComment = commentSaveDto.toEntity();
        checkIfUserAndNotTmpUser(member);

        reComment.setWriter(member);
        reComment.setAdminPost(checkPostExist(postId));
        reComment.setParentComment(checkFirstCommentExists(parentCommentId));

        commentRepository.save(reComment);

        return new ResponseEntity<>("Comment save", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateComment(Long id, Member member, CommentUpdateDto commentUpdateDto) {
        checkIfUserAndNotTmpUser(member);

        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));

        if(!comment.getWriter().getUsername().equals(member.getUsername())){
            log.info(comment.getWriter().getUsername() + " ///" + member.getUsername());
            throw new AccessDeniedException("해당기능을 사용할 수없습니다.");
        }

        comment.updateComment(commentUpdateDto.getContent());

        commentRepository.save(comment);

        return new ResponseEntity<>("Comment update", HttpStatus.OK);

    }

    @Override
    public void deleteComment(Long id, Member member) {
        checkIfUserAndNotTmpUser(member);
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));

        AdminPost adminPost = checkPostExist(comment.getAdminPost().getId());

        if (!comment.getWriter().equals(member)) {
            if (member.getRole() == Role.ADMIN) {
                deleteCommentLogic(member, adminPost, comment);
                return;
            }
            throw new AccessDeniedException("해당기능을 사용할 수없습니다.");
        }

        // 대댓글이 존재하면 내용, 작성자는 Null로 저장, 존재하지 않으면 바로 삭제
        if (!comment.getChildComment().isEmpty()) {
            comment.removeParentCommentExistChildComment();
            commentRepository.save(comment);
            return;
        }
        deleteCommentLogic(member, adminPost, comment);

    }

    private void deleteCommentLogic(Member member, AdminPost adminPost, Comment comment) {
//        member.getCommentList().remove(comment);
//        comment.getChildComment().remove(comment);
        adminPost.getCommentList().remove(comment);
        commentRepository.delete(comment);
    }


    //댓글 작성자 존재하는지 확인
    private void checkIfUserAndNotTmpUser(Member member) {
        if (memberRepository.findByUsername(member.getUsername()).isEmpty()) {
            throw new MemberNotFoundException("존재하지 않는 유저입니다.");
        }

        if (member.getRole().toString().startsWith("TMP")) {
            throw new AccessDeniedException("이메일 인증을 완료하세요");
        }
    }
    // 게시글이 존재하는지 확인
    private AdminPost checkPostExist(Long postId) {
        return adminPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지않습니다."));
    }

    private Comment checkFirstCommentExists(Long parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentNotFoundException("대댓글을 작성할 수 없습니다."));
    }
}
