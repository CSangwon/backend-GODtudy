package com.example.godtudy.domain.member.controller;

import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.service.MemberService;;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final MemberService memberService;

    @PostMapping("/test")
    public String checkCurrentMember(@CurrentMember Member member) {
        log.info("*******************");
        log.info(member.getUsername());
        log.info(member.getNickname());
        log.info(member.getRole().toString());
        log.info(member.getEmail());
        log.info("*******************");
        return "check";
    }

}
