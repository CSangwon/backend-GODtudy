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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    /**
     * 중복 확인
     */
    private boolean isUrlDuplicate(String url){

        List<Study> study = studyRepository.findAllByUrl(url);
        if (study.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 스터디 선생님,학생 확인 (관리자 확인)
     */
    private void checkStudyMember(Member member, Study study) {
        Long memberId = member.getId();
        if (study.getStudent().getId()!=memberId && study.getTeacher().getId()!=memberId && member.getRole()!=Role.ADMIN) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    /**
     * 공부방 생성 (by. Admin)
     */
    @Transactional
    public StudyDto createStudyByAdmin(CreateStudyRequestDto request) {
        Member teacher = memberRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new NoSuchElementException("해당하는 선생님 정보가 없습니다."));

        StudyDto studyDto = createStudyByTeacher(teacher, request);

        return studyDto;
    }

    /**
     * 공부방 생성 (by. Teacher)
     */
    @Transactional
    public StudyDto createStudyByTeacher(Member teacher, CreateStudyRequestDto request) {

        StudyDto studyDto = null;

        if (teacher.getRole() == Role.TEACHER && !isUrlDuplicate(request.getUrl())) {
            Member student = memberRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new NoSuchElementException("해당하는 학생 정보가 없습니다."));

            Study study = Study.builder()
                    .name(request.getName())
                    .url(request.getUrl())
                    .subject(request.getSubject())
                    .teacher(teacher)
                    .student(student)
                    .shortDescription(request.getShortDescription())
                    .build();

            Study newStudy = studyRepository.save(study);
            studyDto = new StudyDto(newStudy);
        }
        return studyDto;
    }

    /**
     * 공부방 조회
     */
    public StudyDto getStudy(Member member, String url) {
        Study study = studyRepository.findByUrl(url);
        checkStudyMember(member,study);
        StudyDto studyDto = new StudyDto(study);
        return studyDto;
    }

    /**
     * 공부방 삭제
     */
    @Transactional
    public String deleteStudy(Member member, String url) {
        Study study = studyRepository.findByUrl(url);
        checkStudyMember(member,study);
        studyRepository.deleteById(study.getId());
        return study.getUrl();
    }

    /**
     * 공부방 수정
     */
    @Transactional
    public StudyDto updateStudy(Member member, String url, UpdateStudyRequestDto request) {
        Study study = studyRepository.findByUrl(url);
        checkStudyMember(member,study);
        study.updateStudy(request);
        StudyDto studyDto = new StudyDto(study);
        return studyDto;
    }
}
