package com.example.godtudy.domain.study.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.study.dto.request.CreateStudyRequestDto;
import com.example.godtudy.domain.study.dto.request.UpdateStudyRequestDto;
import com.example.godtudy.domain.study.dto.response.StudyDto;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional
@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    /**
     * 중복 확인
     */
    @Transactional(readOnly = true)
    public boolean isUrlDuplicate(String url){

        List<Study> study = studyRepository.findAllByUrl(url);
        if (study.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 공부방 생성 (by. Admin)
     */
    public StudyDto createStudyByAdmin(CreateStudyRequestDto request) {
        Member teacher = memberRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new NoSuchElementException("해당하는 선생님 정보가 없습니다."));

        StudyDto studyDto = createStudyByTeacher(teacher, request);

        return studyDto;
    }

    /**
     * 공부방 생성 (by. Teacher)
     */
    public StudyDto createStudyByTeacher(Member teacher, CreateStudyRequestDto request) {

        StudyDto studyDto = null;

        if (teacher.getRole() == Role.TEACHER && !isUrlDuplicate(request.getUrl())) {
            Member student = memberRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new NoSuchElementException("해당하는 학생 정보가 없습니다."));

            studyDto = new StudyDto(request);

            Study newStudy = Study.builder()
                    .name(request.getName())
                    .url(request.getUrl())
                    .subject(request.getSubject())
                    .teacher(teacher)
                    .student(student)
                    .shortDescription(request.getShortDescription())
                    .build();

            studyRepository.save(newStudy);
        }
        return studyDto;
    }

    @Transactional(readOnly = true)
    public StudyDto getStudy(String url) {
        Study study = studyRepository.findByUrl(url);
        return new StudyDto(study);
    }

    public String deleteStudy(String url) {
        Study study = studyRepository.findByUrl(url);
        studyRepository.deleteById(study.getId());
        return study.getUrl();
    }

    public StudyDto updateStudy(String url, UpdateStudyRequestDto request) {
        Study study = studyRepository.findByUrl(url);

        study.updateStudy(request);
        StudyDto studyDto = new StudyDto(study);

        return studyDto;
    }

    // TODO : 학생검색(member에서 이름으로 조회 구현)
}
