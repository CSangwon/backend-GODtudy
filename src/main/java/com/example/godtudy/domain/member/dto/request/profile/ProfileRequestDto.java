package com.example.godtudy.domain.member.dto.request.profile;

import com.example.godtudy.domain.member.entity.Subject;
import com.example.godtudy.global.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDto {

    @NotBlank(message = "닉네임을 입력해주세요", groups = ValidationGroups.NotEmptyGroup.class)
    @Pattern(regexp = "^[가-힣|a-z|A-Z|0-9|]{4,10}",
            message = "닉네임은 한글, 영어, 숫자만 4 ~10자리로 입력 가능합니다"
            , groups = ValidationGroups.PatternCheckGroup.class)
    private String nickname;

    @Length(max=35)
    private String bio;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImageUrl;

    private List<Subject> subject = new ArrayList<>();
}
