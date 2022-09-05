package com.example.godtudy.domain.member.dto.response;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Subject;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {

    private String username;

    private String email;

    private String nickname;

    private String bio;

    private String profileImageUrl;

    @Builder.Default
    private List<SubjectEnum> subjectList = new ArrayList<>();

    public ProfileResponseDto(Member member) {
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.bio = member.getBio();
        this.profileImageUrl = member.getProfileImageUrl();
        this.subjectList = member.getSubject().stream().map(Subject::getTitle).collect(Collectors.toList());
    }

}
