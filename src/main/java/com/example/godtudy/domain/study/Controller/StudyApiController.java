package com.example.godtudy.domain.study.Controller;

import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.dto.request.UpdateStudyRequestDto;
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
     * 공부방 생성 (by. teacher)
     */
    @PostMapping("/")
    public ResponseEntity<StudyDto> createStudy(@CurrentMember Member member,
                                                @RequestBody CreateStudyRequestDto request) {

        StudyDto response = null;

        try {
            response = studyService.createStudyByTeacher(member, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 공부방 조회
     */
    @GetMapping("/{url}")
    public ResponseEntity<StudyDto> getStudy(@PathVariable("url") String url) {
         StudyDto response = studyService.getStudy(url);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 공부방 수정
     */
    @PutMapping("{/{url}")
    public ResponseEntity<StudyDto> updateStudy(@PathVariable("url") String url,
                                              @RequestBody UpdateStudyRequestDto request) {
        StudyDto studyDto = studyService.updateStudy(url, request);
        return new ResponseEntity<>(studyDto, HttpStatus.OK);
    }

    /**
     * 공부방 삭제
     */
    @DeleteMapping("/{url}")
    public ResponseEntity<String> deleteStudy(@PathVariable("url") String url) {
        String deleteUrl = studyService.deleteStudy(url);
        return new ResponseEntity<>(deleteUrl, HttpStatus.OK);
    }

    /**
     * 공부방 생성 (by.admin)
     */
    @PostMapping("/admin/")
    public ResponseEntity<StudyDto> createStudyByAdmn(@RequestBody CreateStudyRequestDto request) {

        StudyDto response = null;

        try {
            response = studyService.createStudyByAdmin(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
