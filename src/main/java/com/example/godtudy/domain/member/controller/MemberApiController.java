package com.example.godtudy.domain.member.controller;

import com.example.godtudy.domain.member.dto.request.*;
import com.example.godtudy.domain.member.dto.request.profile.FindPasswordRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.FindUsernameRequestDto;
import com.example.godtudy.domain.member.dto.response.profile.FindUsernameResponseDto;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.validator.MemberJoinFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/member")
@RequiredArgsConstructor
@RestController
@Slf4j
public class MemberApiController {

    private final MemberService memberService;
    private final MemberJoinFormValidator memberJoinFormValidator;

    @ExceptionHandler({
            IllegalStateException.class
    })

    @InitBinder("memberJoinForm")
    public void initBinder1(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(memberJoinFormValidator);
    }


    /*   로그인   */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto memberLoginRequestDto) {
        return memberService.login(memberLoginRequestDto);
    }

    /*   accesstoken만료 시 재발급   */
    @PostMapping("/auth/token")
    public ResponseEntity<?> reissueAuthenticationToken(@RequestBody TokenRequestDto tokenRequestDto) {
        return memberService.reissueAuthenticationToken(tokenRequestDto);
    }

    /*   로그아웃   */
    @PostMapping("/logout")
    public void logout(@RequestBody MemberLogoutRequestDto memberLogoutRequestDto) {
        memberService.logout(memberLogoutRequestDto);
    }

    /*     회원가입     */
    @PostMapping("/join/{role}")
    public ResponseEntity<?> joinMember(@Valid @RequestBody MemberJoinForm memberJoinForm, @PathVariable String role, Errors errors) {
        if (errors.hasErrors()) {
            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Member member = memberService.initJoinMember(memberJoinForm, role.toUpperCase());

        memberService.sendJoinConfirmEmail(member);

        return new ResponseEntity<>("Join Success", HttpStatus.OK);
    }

    /*   이메일 인증   */
    @GetMapping("/checkEmailToken/{token}/{email}")
    public ResponseEntity<?> checkEmailToken(@PathVariable String token, @PathVariable String email) {
        return memberService.checkEmailToken(token, email);
    }

    /*     아이디 중복 확인     */
    @PostMapping("/join/student/username")
    public ResponseEntity<?> usernameCheck(@Valid @RequestBody UsernameRequestDto usernameRequestDto, Errors errors) {
        memberService.usernameCheckDuplication(usernameRequestDto);
        if (errors.hasErrors()) {
            return new ResponseEntity<>("Invalid Username", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Check Username", HttpStatus.OK);
    }

    /*     이메일 중복 확인     */
    @PostMapping("/join/student/email")
    public ResponseEntity<?> emailCheck(@Valid @RequestBody EmailRequestDto emailRequestDto, Errors errors) {
        memberService.emailCheckDuplication(emailRequestDto);
        if (errors.hasErrors()) {
            return new ResponseEntity<>("Invalid email", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Check Username", HttpStatus.OK);
    }

    /*     닉네임 중복 확인     */
    @PostMapping("/join/student/nickname")
    public ResponseEntity<?> nicknameCheck(@Valid @RequestBody NicknameRequestDto nicknameRequestDto, Errors errors) {
        memberService.nicknameCheckDuplication(nicknameRequestDto);
        if (errors.hasErrors()) {
            return new ResponseEntity<>("Invalid email", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Check Username", HttpStatus.OK);
    }

    @PostMapping("/find/username")
    public ResponseEntity<?> findUsernameByEmail(@RequestBody FindUsernameRequestDto findUsernameRequestDto) {
        FindUsernameResponseDto username = memberService.findUsername(findUsernameRequestDto);

        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @PostMapping("/find/password")
    public ResponseEntity<?> findPasswordByEmail(@RequestBody FindPasswordRequestDto findPasswordRequestDto) {
        return memberService.findPassword(findPasswordRequestDto);
    }
}
