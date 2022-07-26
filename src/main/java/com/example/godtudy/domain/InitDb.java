package com.example.godtudy.domain;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.entity.Subject;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.repository.SubjectRepository;
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

    private final MemberInitService memberInitService;
    private static final int COUNT = 5;

    @PostConstruct
    public void init() {
        memberInitService.insertMember();
        memberInitService.insertSubject();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class MemberInitService {
        private final MemberRepository memberRepository;
        private final SubjectRepository subjectRepository;
        private final PasswordEncoder passwordEncoder;
        Random random = new Random();

        public void insertMember() {
            IntStream.rangeClosed(1,COUNT).forEach(i -> {
//                Member swchoi2997 = Member.builder()
//                        .name("최상원")
//                        .username("swchoi2997")
//                        .password(passwordEncoder.encode("swchoi2997"))
//                        .email("swchoi2997")
//                        .nickname("숲속의1냉면")
//                        .birthday(LocalDate.of(1997, 02, 12))
//                        .role(Role.ADMIN)
//                        .emailVerified(true)
//                        .emailCheckTokenGeneratedAt(LocalDateTime.now())
//                        .build();
//                memberRepository.save(swchoi2997);

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
    }


}
