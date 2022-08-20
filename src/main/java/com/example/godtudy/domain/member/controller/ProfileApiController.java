package com.example.godtudy.domain.member.controller;

import com.example.godtudy.domain.member.dto.request.profile.PasswordUpdateRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.member.dto.response.ProfileResponseDto;
import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    //본인 프로필 가져오기
    @GetMapping("/{profileId}")
    public ResponseEntity<ProfileResponseDto> profileInquiry(@CurrentMember Member member, @PathVariable String profileId) {
        ProfileResponseDto profile = profileService.getProfile(member);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    //프로필 업데이트
    @PutMapping("")
    public ResponseEntity<?> updateProfile(@CurrentMember Member member, @Valid @RequestBody ProfileRequestDto profileRequestDto) {
        profileService.updateProfile(member, profileRequestDto);
        return new ResponseEntity<>("update Ok", HttpStatus.OK);
    }

    //비밀번호 업데이트
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@CurrentMember Member member, @RequestHeader("X-AUTH-TOKEN") String accessToken,
                                            @Valid @RequestBody PasswordUpdateRequestDto passwordUpdateRequestDto) {
        profileService.updatePassword(member, passwordUpdateRequestDto, accessToken);
        return new ResponseEntity<>("Password Update", HttpStatus.OK);
    }

    //프로필 목록 검색
    @GetMapping("/list")
    public ResponseEntity<?> getProfiles(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "role") Role role,
            @PageableDefault(size = 12, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ProfileResponseDto> profileResponseDtoPage = profileService.searchMember(username, name, role, pageable);
        return new ResponseEntity<>(profileResponseDtoPage, HttpStatus.OK);
    }
}
