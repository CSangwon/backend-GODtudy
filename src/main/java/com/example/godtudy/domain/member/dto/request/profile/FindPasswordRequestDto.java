package com.example.godtudy.domain.member.dto.request.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindPasswordRequestDto {

    private String name;

    private String username;

    private String email;
}
