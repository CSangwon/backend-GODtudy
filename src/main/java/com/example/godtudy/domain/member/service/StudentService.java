package com.example.godtudy.domain.member.service;

import com.example.godtudy.domain.member.dto.request.MemberSearchRequestDto;
import com.example.godtudy.domain.member.dto.response.MemberSearchResponseDto;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StudentService {

    private final MemberRepository memberRepository;
    /**
     * 학생 검색
     */
    public List<MemberSearchResponseDto> searchStudent(MemberSearchRequestDto request){
        List<MemberSearchResponseDto> response = new ArrayList<>();
        List<Member> studentList = memberRepository.findByUsernameContainsAndRole(request.getUsername(), Role.STUDENT);
        for (Member member : studentList) {
            MemberSearchResponseDto memberSearchResponseDto = new MemberSearchResponseDto(member);
            response.add(memberSearchResponseDto);
        }
        return response;
    }
}
