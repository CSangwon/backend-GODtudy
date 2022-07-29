package com.example.godtudy.domain.member.dto.response;

import com.example.godtudy.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MemberSearchResponseDto {

    private Long id;
    private String name;
    private String username;

    public MemberSearchResponseDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.username = member.getUsername();
    }

    //    List<MemberSearchInfo> memberSearchInfoList = new ArrayList<>();
//
//    class MemberSearchInfo {
//        private Long id;
//        private String name;
//        private String username;
//
//        public MemberSearchInfo(Long id, String name, String username) {
//            this.id = id;
//            this.name = name;
//            this.username = username;
//        }
//    }

}
