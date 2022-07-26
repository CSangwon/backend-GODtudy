package com.example.godtudy.domain.member.dto.request.profile;

import com.example.godtudy.global.validation.ValidationGroups;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class PasswordUpdateRequestDto {

    @NotBlank(message = "비밀번호는 필수 입력사항입니다..", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[0-9a-zA-Z])(?=.*[~!@#$%^&*()=+])[0-9a-zA-Z\\d~!@#$%^&*()=+]{8,20}",
            message = "비밀번호는 영문과 숫자 특수문자 조합으로 8 ~ 20자리로 설정해주세요."
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.", groups = ValidationGroups.NotEmptyGroup.class)
    private String newPasswordConfirm;
}
