package com.example.godtudy.domain.member.controller;

import com.example.godtudy.domain.member.dto.request.profile.PasswordUpdateRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.member.dto.response.ProfileResponseDto;
import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileApiController {

    private final ProfileService profileService;

    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponseDto> profileInquiry(@CurrentMember Member member, @PathVariable String profileId) {
        ProfileResponseDto profile = profileService.getProfile(member);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    //프로필 업데이트
    @PostMapping("/update/")
    public ResponseEntity<?> updateProfile(@CurrentMember Member member, @Valid ProfileRequestDto profileRequestDto) {
        profileService.updateProfile(member, profileRequestDto);
        return new ResponseEntity<>("update Ok", HttpStatus.OK);
    }

    //비밀번호 업데이트
    @PostMapping("/update/password")
    public ResponseEntity<?> updatePassword(@CurrentMember Member member, @Valid PasswordUpdateRequestDto passwordUpdateRequestDto) {
        profileService.updatePassword(member, passwordUpdateRequestDto);
        return new ResponseEntity<>("Password Update", HttpStatus.OK);
    }



}
