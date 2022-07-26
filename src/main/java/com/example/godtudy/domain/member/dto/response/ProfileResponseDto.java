package com.example.godtudy.domain.member.dto.response;

import com.example.godtudy.domain.member.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //    private Set<Subject> subject = new HashSet<>();
    private List<Subject> subject = new ArrayList<>();



}
