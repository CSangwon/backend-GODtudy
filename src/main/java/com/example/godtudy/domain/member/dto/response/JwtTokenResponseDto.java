package com.example.godtudy.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponseDto {

    private String accessToken;

    private String refreshToken;

}
