package com.example.godtudy.domain.study.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.dto.response.StudyDto;
import com.example.godtudy.domain.study.dto.response.CreateStudyResponseDto;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    /**
     * 중복 확인
     */
    public boolean isUrlDuplicate(String url){

        List<Study> study = studyRepository.findAllByUrl(url);
        if (study.isEmpty()) {
            return false;
        }
        return true;
    }

    public CreateStudyResponseDto createStudy(Member teacher, CreateStudyRequestDto request) {

        CreateStudyResponseDto response = null;

        if (teacher.getRole() == Role.TEACHER && !isUrlDuplicate(request.getUrl())) {
            Member student = memberRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new NoSuchElementException("해당하는 학생 정보가 없습니다."));

            StudyDto studyDto = new StudyDto(request);
            studyDto.setTeacher(teacher);
            studyDto.setStudent(student);

            Study newStudy = studyDto.toEntity();

            studyRepository.save(newStudy);

            response = CreateStudyResponseDto.builder()
                    .url(newStudy.getUrl())
                    .build();
        }
        return response;
    }

    public StudyDto getStudy(String url) {
        Study study = studyRepository.findByUrl(url);
        return new StudyDto(study);
    }

    // TODO : 학생조회, 학생검색(이름), 공부방생성(admin), 공부방생성, 공부방수정(이름, 설명), 공부방 삭제
}
