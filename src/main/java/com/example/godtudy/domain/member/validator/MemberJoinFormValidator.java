package com.example.godtudy.domain.member.validator;

import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.dto.request.MemberJoinForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class MemberJoinFormValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(MemberJoinForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberJoinForm memberJoinForm = (MemberJoinForm) target;

        if (memberRepository.existsByUsername(memberJoinForm.getUsername())) {
            errors.rejectValue("username", "invalid username", new Object[]{memberJoinForm.getUsername()}, "이미 사용중인 아이디 입니다.");
        }
        if (memberRepository.existsByEmail(memberJoinForm.getEmail())) {
            errors.rejectValue("email", "invalid email", new Object[]{memberJoinForm.getEmail()}, "이미 등록된 이메일 입니다.");
        }
        if (memberRepository.existsByNickname(memberJoinForm.getNickname())) {
            errors.rejectValue("nickname", "invalid nickname", new Object[]{memberJoinForm.getNickname()}, "이미 사용중인 닉네임 입니다.");
        }

        if (!memberJoinForm.getPassword().equals(memberJoinForm.getPasswordConfirm())) {
            errors.rejectValue("password", "invalid password", new Object[]{memberJoinForm.getPasswordConfirm()}, "비밀번호를 확인해 주세요");
        }

    }
}
