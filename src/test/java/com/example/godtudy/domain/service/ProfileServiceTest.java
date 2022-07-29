package com.example.godtudy.domain.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.dto.request.profile.PasswordUpdateRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.member.dto.response.ProfileResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class ProfileServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProfileService profileService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @WithMember("swchoi1997")
    @DisplayName("WithMember 테스트")
    @Test
    public void withMemberTest() throws Exception{
        Optional<Member> member = memberRepository.findByUsername("swchoi1997");
        assertThat(member.isPresent()).isTrue();
    }

    @WithMember("swchoi1997")
    @DisplayName("개인정보 가져오기")
    @Test
    public void getProfileTest() throws Exception{
        //given
        Optional<Member> member = memberRepository.findByUsername("swchoi1997");

        //when
        ProfileResponseDto memberProfile = profileService.getProfile(member.get());

        //then
        assertThat(memberProfile.getUsername()).isEqualTo("swchoi1997");
        assertThat(memberProfile.getEmail()).isEqualTo("swchoi1997@naver.com");
        assertThat(memberProfile.getNickname()).isEqualTo("숲속의냉면");
        assertThat(memberProfile.getBio()).isNull();
        assertThat(memberProfile.getProfileImageUrl()).isNull();
    }

    @WithMember("swchoi1997")
    @DisplayName("프로필 업데이트")
    @Test
    public void profileUpdate() throws Exception {
        //given
        Optional<Member> member = memberRepository.findByUsername("swchoi1997");
        Member member1 = member.get();

        ProfileRequestDto profileRequestDto = ProfileRequestDto.builder()
                .nickname("숲속의짜장")
                .bio("test")
                .profileImageUrl("test")
                .build();

        //when
        profileService.updateProfile(member1, profileRequestDto);

        //then
        assertThat(member1.getNickname()).isEqualTo("숲속의짜장");
        assertThat(member1.getBio()).isEqualTo("test");
        assertThat(member1.getProfileImageUrl()).isEqualTo("test");
    }

    @WithMember("swchoi1997")
    @DisplayName("비밀번호 업데이트")
    @Test
    public void passwordUpdate() throws Exception {
        //given
        Optional<Member> member = memberRepository.findByUsername("swchoi1997");
        Member member1 = member.get();

        PasswordUpdateRequestDto passwordUpdateRequestDto = PasswordUpdateRequestDto.builder()
                .newPassword("swchoi1997")
                .newPasswordConfirm("swchoi1997")
                .build();

        org.junit.jupiter.api.Assertions.assertFalse(passwordEncoder.matches("swchoi1997", member1.getPassword()));
        //when
        profileService.updatePassword(member1, passwordUpdateRequestDto);

        //then
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("swchoi1997", member1.getPassword()));
    }



}