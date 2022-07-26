package com.example.godtudy.domain.member.dto.request;

import com.example.godtudy.domain.member.entity.Subject;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.global.validation.ValidationGroups;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinForm {

    @NotBlank(message = "아이디는 필수 입력사항입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "[a-zA-Z0-9]{2,15}",
            message = "아이디는 영문, 숫자 조합만 가능하며 2 ~ 15 자리 까지 가능합니다."
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력사항입니다..", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[0-9a-zA-Z])(?=.*[~!@#$%^&*()=+])[0-9a-zA-Z\\d~!@#$%^&*()=+]{8,20}",
            message = "비밀번호는 영문과 숫자 특수문자 조합으로 8 ~ 20자리로 설정해주세요."
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수 입력사항입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "[가-힣]{2,4}",
            message = "올바른 이름을 입력해주세요"
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String name;

    @NotBlank(message = "이메일은 필수 입력사항입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}",
            message = "올바르지 않은 이메일 형식입니다."
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[가-힣|a-z|A-Z|0-9|]{4,10}",
            message = "닉네임은 한글, 영어, 숫자만 4 ~10자리로 입력 가능합니다"
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String nickname;

    @NotBlank(message = "*", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[0-9|]{4}",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String year;

    private String month;

    @NotBlank(message = "*", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^([1-9]|[12][0-9]|3[01]){1,2}",
            groups = ValidationGroups.PatternCheckGroup.class)
    private String day;

    private Role role;

    @NotBlank(message = "필수 입력사항 입니다.", groups = ValidationGroups.NotEmptyGroup.class)
//    private Set<SubjectEnum> subject;
    private List<SubjectEnum> subject;

    public Member toEntity(){
        String birthday = year + "-" + month + "-" + day;
        LocalDate date = LocalDate.parse(birthday, DateTimeFormatter.ISO_DATE);

        return Member.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .nickname(nickname)
                .birthday(date)
                .role(role)
                .subject(new ArrayList<>())
                .adminPosts(new ArrayList<>())
                .emailVerified(false)
                .build();
    }

}
