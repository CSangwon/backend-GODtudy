package com.example.godtudy.domain;

import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.entity.Subject;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.repository.SubjectRepository;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.AdminPostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final MemberInitService initService;
    private final static int COUNT = 6;
    private final static int POST_COUNT = 10;
    private final static int COMMENT_COUNT = 50;


    @PostConstruct
    public void init() {
        initService.insertMember();
        initService.insertSubject();
        initService.insertPost();
        initService.insertComment();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class MemberInitService {
        private final MemberRepository memberRepository;
        private final SubjectRepository subjectRepository;
        private final AdminPostRepository adminPostRepository;
        private final CommentRepository commentRepository;
        private final PasswordEncoder passwordEncoder;
        Random random = new Random();

        public void insertMember() {
            IntStream.rangeClosed(1,COUNT).forEach(i -> {
                Member member = Member.builder()
                        .name("name" + i)
                        .username("test" + i)
                        .password(passwordEncoder.encode("test" + i))
                        .email("test" + i + "@gmail.com")
                        .nickname("nickname" + i)
                        .birthday(LocalDate.of(1960 + random.nextInt(60),
                                1 + random.nextInt(12),
                                1 + random.nextInt(28)))
                        .role(Role.values()[random.nextInt(Role.values().length - 3)])
                        .build();

                memberRepository.save(member);
            });
        }

        public void insertSubject() {
            IntStream.rangeClosed(1,COUNT).forEach(i -> {
                Member member = Member.builder()
                        .id((long)(1 + random.nextInt(COUNT)))
                        .build();

                Subject subject = Subject.builder()
                        .title(SubjectEnum.values()[random.nextInt(SubjectEnum.values().length)])
                        .member(member)
                        .build();

                subjectRepository.save(subject);
            });
        }

        public void insertPost(){
            IntStream.rangeClosed(1, POST_COUNT).forEach(i -> {
                AdminPost adminPost = AdminPost.builder()
                        .title("title" + i)
                        .content("content" + i)
                        .noticeOrEvent(AdminPostEnum.NOTICE)
                        .member(memberRepository.findById(Long.valueOf(random.nextInt(5) + 1)).orElse(null))
                        .build();

                adminPostRepository.save(adminPost);
            });
        }

        public void insertComment() {
            IntStream.rangeClosed(1, COMMENT_COUNT).forEach(i -> {
                Comment comment = Comment.builder()
                        .content("content" + i)
                        .adminPost(adminPostRepository.findById(Long.valueOf(random.nextInt(10) + 1)).orElse(null))
                        .writer(memberRepository.findById(Long.valueOf(random.nextInt(5) + 1)).orElse(null))
                        .build();

                commentRepository.save(comment);
            });

            commentRepository.findAll().stream().forEach(comment -> {
                IntStream.rangeClosed(1, COMMENT_COUNT).forEach(i -> {
                    Comment reComment = Comment.builder()
                            .content("reContent" + i)
                            .adminPost(comment.getAdminPost())
                            .writer(memberRepository.findById(Long.valueOf(random.nextInt(5) + 1)).orElse(null))
                            .parentComment(comment)
                            .build();

                    commentRepository.save(reComment);
                });
            });
        }

    }


}


