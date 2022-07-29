package com.example.godtudy.domain.comment.service;

import com.example.godtudy.domain.comment.dto.CommentSaveDto;
import com.example.godtudy.domain.comment.dto.CommentUpdateDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.example.godtudy.global.advice.exception.MemberNotFoundException;
import com.example.godtudy.global.advice.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final AdminPostRepository adminPostRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


    @Override
    public void save(Long postId, Member member, CommentSaveDto commentSaveDto) {
        Comment comment = commentSaveDto.toEntity();
        checkIfUserAndNotTmpUser(member);

        comment.setWriter(member);
        comment.setAdminPost(checkPostExist(postId));

        commentRepository.save(comment);
    }

    @Override
    public void saveReComment(Long postId, Member member, Long firstCommentId, CommentSaveDto commentSaveDto) {

    }

    @Override
    public void update(Long id, Member member, CommentUpdateDto commentUpdateDto) {

    }

    @Override
    public void delete(Long id, Member member) {

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

    private AdminPost checkPostExist(Long postId) {
        return adminPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(" 해당 게시글이 존재하지않습니다."));
    }
}
