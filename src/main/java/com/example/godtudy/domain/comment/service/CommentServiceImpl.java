package com.example.godtudy.domain.comment.service;

import com.example.godtudy.domain.comment.dto.request.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.request.CommentUpdateDto;
import com.example.godtudy.domain.comment.dto.response.CommentResponseDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.PostEnum;
import com.example.godtudy.domain.post.entity.StudyPost;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.example.godtudy.domain.post.repository.StudyPostRepository;
import com.example.godtudy.global.advice.exception.CommentNotFoundException;
import com.example.godtudy.global.advice.exception.MemberNotFoundException;
import com.example.godtudy.global.advice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final AdminPostRepository adminPostRepository;
    private final StudyPostRepository studyPostRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


    @Override
    public CommentResponseDto saveComment(String postType, Long postId, Member member, CommentSaveDto commentSaveDto) {
        Comment comment = saveCommentBefore(postType, postId, member, commentSaveDto);
        comment.setIsFirstComment();
        commentRepository.save(comment);

        return CommentResponseDto.builder().
                id(comment.getId()).content(comment.getContent()).username(member.getUsername()).postKind(postType).message("Comment Save").
                build();
    }


    @Override
    public CommentResponseDto saveReComment(String postType, Long postId, Member member, Long parentCommentId, CommentSaveDto commentSaveDto) {
        Comment reComment = saveCommentBefore(postType, postId, member, commentSaveDto);
        reComment.setParentComment(checkFirstCommentExists(parentCommentId));
        commentRepository.save(reComment);

        return CommentResponseDto.builder().
                id(reComment.getId()).content(reComment.getContent()).username(member.getUsername()).postKind(postType).message("ReComment Save").
                build();
    }

    @Override
    public CommentResponseDto updateComment(String postType, Long commentId, Member member, CommentUpdateDto commentUpdateDto) {
        checkExistUserAndNotTmpUser(member);
        Comment comment = updateAndDeleteBefore(commentId, member);

        comment.updateComment(commentUpdateDto.getContent());
        commentRepository.save(comment);

        return CommentResponseDto.builder().
                id(comment.getId()).content(comment.getContent()).username(member.getUsername()).postKind(postType).message("update Complete").
                build();
    }

    @Override
    public void deleteComment(String postType, Long commentId, Member member) {
        checkExistUserAndNotTmpUser(member);
        Comment comment = updateAndDeleteBefore(commentId, member);

        Object postKind = checkPostExist(postType, comment.getAdminPost().getId());

        // 대댓글이 존재하면 내용, 작성자는 Null로 저장, 존재하지 않으면 바로 삭제
        if (!comment.getChildComment().isEmpty()) {
            comment.removeParentCommentExistChildComment();
            commentRepository.save(comment);
            return;
        }
        deleteCommentLogic(member, postKind, comment);

    }

    private void deleteCommentLogic(Member member, Object postKind, Comment comment) {
        if (postKind instanceof AdminPost) {
            ((AdminPost) postKind).getCommentList().remove(comment);
        } else{
            ((StudyPost) postKind).getCommentList().remove(comment);
        }
        commentRepository.delete(comment);
    }

    // 댓글 작성 전에 해야할 것
    private Comment saveCommentBefore(String postType, Long postId, Member member, CommentSaveDto commentSaveDto) {
        checkExistUserAndNotTmpUser(member);
        Comment comment = commentSaveDto.toEntity();
        comment.setWriter(member);
        comment.checkAdminPostOrStudyPost(checkPostExist(postType, postId));

        return comment;
    }

    // 삭제 갱신 전에 확인해야할 것
    private Comment updateAndDeleteBefore(Long id, Member member) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));
        if (comment.getWriter() != member) {
            if (member.getRole() == Role.ADMIN) {
                return comment;
            }
            throw new AccessDeniedException("해당기능을 사용할 수없습니다.");
        }

        return comment;
    }


    //댓글 작성자 존재하는지 확인
    private void checkExistUserAndNotTmpUser(Member member) {
        if (memberRepository.findByUsername(member.getUsername()).isEmpty()) {
            throw new MemberNotFoundException("존재하지 않는 유저입니다.");
        }

        if (member.getRole().toString().startsWith("TMP")) {
            throw new AccessDeniedException("이메일 인증을 완료하세요");
        }
    }
    // 게시글이 존재하는지 확인
    private Object checkPostExist(String postType, Long postId) {
        if((postType.toUpperCase()).equals(PostEnum.EVENT.toString()) || (postType.toUpperCase()).equals(PostEnum.NOTICE.toString())){
            PostType<AdminPost> adminPostType = new PostType<>(adminPostRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지않습니다.")));
            return adminPostType.getPostType();
        } else{
            PostType<StudyPost> studyPostType = new PostType<>(studyPostRepository.findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("해당 게시글이 존재하지 않습니다.")));
            return studyPostType.getPostType();
        }


    }

    private Comment checkFirstCommentExists(Long parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentNotFoundException("대댓글을 작성할 수 없습니다."));
    }
}


class PostType<T>{
    private T postType;

    public PostType(T postType) {
        this.postType = postType;
    }

    public T getPostType() {
        return postType;
    }
}
