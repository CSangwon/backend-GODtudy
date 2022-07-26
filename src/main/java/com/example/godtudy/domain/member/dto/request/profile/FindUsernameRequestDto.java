package com.example.godtudy.domain.member.dto.request.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class FindUsernameRequestDto {

    private String name;

    private String email;

}
