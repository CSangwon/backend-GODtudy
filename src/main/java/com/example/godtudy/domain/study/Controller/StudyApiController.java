package com.example.godtudy.domain.study.Controller;

import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.dto.response.CreateStudyResponseDto;
import com.example.godtudy.domain.study.dto.response.StudyDto;
import com.example.godtudy.domain.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/study")
@RequiredArgsConstructor
@RestController
public class StudyApiController {

    private final StudyService studyService;

    /**
     * 공부방 생성
     */
    @PostMapping("/new")
    public ResponseEntity<CreateStudyResponseDto> createStudy(@CurrentMember Member member,
                                                              @RequestBody CreateStudyRequestDto request) {

        CreateStudyResponseDto response = null;

        try {
            response = studyService.createStudy(member, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 공부방 조회
     */
    @GetMapping("{url}")
    public ResponseEntity<StudyDto> getStudy(@PathVariable("url") String url) {
        StudyDto response = studyService.getStudy(url);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
