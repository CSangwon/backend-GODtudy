package com.example.godtudy.domain.member.service;

import com.example.godtudy.domain.member.dto.request.MemberLogoutRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.PasswordUpdateRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.member.dto.response.ProfileResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public ProfileResponseDto getProfile(Member member) {
        return new ProfileResponseDto(member);
    }

    public void updateProfile(Member member, ProfileRequestDto profileRequestDto) {
        member.updateProfile(profileRequestDto);
        memberRepository.save(member);
    }

    public void updatePassword(Member member, PasswordUpdateRequestDto passwordUpdateRequestDto) {
        if (!passwordUpdateRequestDto.getNewPassword().equals(passwordUpdateRequestDto.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        //비밀번호 업데이트
        member.updatePassword(passwordEncoder.encode(passwordUpdateRequestDto.getNewPassword()));
        memberRepository.save(member);
        //업데이트 후 로그아웃
        memberService.logout(new MemberLogoutRequestDto(member.getUsername()));
    }

    public Page<ProfileResponseDto> searchMember(String username, Role role, Pageable pageable) {
        Page<Member> members = memberRepository.findByUsernameContainsAndRole(username, role, pageable);
        Page<ProfileResponseDto> memberList = members.map(entity -> new ProfileResponseDto(entity));

        return memberList;
    }
}
