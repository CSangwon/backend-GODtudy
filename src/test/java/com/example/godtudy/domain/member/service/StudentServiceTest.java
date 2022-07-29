package com.example.godtudy.domain.member.service;

import com.example.godtudy.domain.member.dto.request.MemberSearchRequestDto;
import com.example.godtudy.domain.member.dto.response.MemberSearchResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int COUNT = 5;

    @BeforeEach
    void createStudentMember() {
        IntStream.rangeClosed(1,COUNT).forEach(i -> {
            Member member = Member.builder()
                    .name("user" + i)
                    .username("user" + i)
                    .password(passwordEncoder.encode("user" + i))
                    .email("user" + i + "@gmail.com")
                    .nickname("nickname" + i)
                    .birthday(LocalDate.of(2010, 2, 12))
                    .role(Role.STUDENT)
                    .build();

            memberRepository.save(member);
        });
    }

    @DisplayName("학생 검색 - 성공")
    @Test
    public void 학생_검색() throws Exception {
        //given
        String username1 = "se";
        String username2 = "r1";

        MemberSearchRequestDto request1 = MemberSearchRequestDto.builder()
                .username(username1)
                .build();

        MemberSearchRequestDto request2 = MemberSearchRequestDto.builder()
                .username(username2)
                .build();

        //when
        List<MemberSearchResponseDto> responseAll = studentService.searchStudent(request1);
        List<MemberSearchResponseDto> responseOne = studentService.searchStudent(request2);

        //then
        assertEquals(responseAll.size(),COUNT);
        assertEquals(responseOne.get(0).getUsername(), "user1");
    }

}