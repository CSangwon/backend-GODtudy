package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.dto.response.PostPagingDto;
import com.example.godtudy.domain.post.dto.response.PostResponseDto;
import com.example.godtudy.domain.post.entity.StudyPost;
import com.example.godtudy.domain.post.repository.StudyPostRepository;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.domain.study.repository.StudyRepository;
import com.example.godtudy.global.advice.exception.MemberNotFoundException;
import com.example.godtudy.global.advice.exception.PostNotFoundException;
import com.example.godtudy.global.advice.exception.StudyNotFoundException;
import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.FileRepository;
import com.example.godtudy.global.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudyPostServiceImpl implements StudyPostService{

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyPostRepository studyPostRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    //게시글 작성 - 파일 없음
    @Override
    public PostResponseDto createPost(Member member, String post, String studyUrl, PostSaveRequestDto postSaveRequestDto) {
        studyPostBefore(member, studyUrl);
        StudyPost studyPost = createPostBefore(member, studyUrl, post, postSaveRequestDto);
        studyPostRepository.save(studyPost);

        return PostResponseDto.builder()
                .title(studyPost.getTitle())
                .content(studyPost.getContent())
                .author(studyPost.getMember().getUsername())
                .postEnum(studyPost.getPostEnum())
                .build();
    }

    //게시글 작성 - 파일 있음
    @Override
    public PostResponseDto createPost(Member member, String post, String studyUrl, List<MultipartFile> files, PostSaveRequestDto postSaveRequestDto) throws IOException {
        studyPostBefore(member, studyUrl);
        StudyPost studyPost = createPostBefore(member,studyUrl, post, postSaveRequestDto);
        saveFile(studyPost, files);

        studyPostRepository.save(studyPost);

        return PostResponseDto.builder()
                .title(studyPost.getTitle())
                .content(studyPost.getContent())
                .author(studyPost.getMember().getUsername())
                .files(studyPost.getFiles())
                .postEnum(studyPost.getPostEnum())
                .build();
    }

    // 게시글 수정 - 파일 없음
    @Override
    public PostResponseDto updatePost(Member member, String post, String studyUrl, Long id, PostUpdateRequestDto postUpdateRequestDto) {
        studyPostBefore(member, studyUrl);
        validPostUpdateRequestDto(postUpdateRequestDto);
        StudyPost studyPost = updatePostBefore(member, studyUrl, post, id);
        studyPost.updateStudyPost(postUpdateRequestDto);

        studyPostRepository.save(studyPost);

        return PostResponseDto.builder()
                .title(studyPost.getTitle())
                .content(studyPost.getContent())
                .author(studyPost.getMember().getUsername())
                .postEnum(studyPost.getPostEnum())
                .build();
    }

    // 게시글 수정 - 파일 있음
    @Override
    public PostResponseDto updatePost(Member member, String post, String studyUrl, List<MultipartFile> files, Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException {
        studyPostBefore(member, studyUrl);
        validPostUpdateRequestDto(postUpdateRequestDto);
        StudyPost studyPost = updatePostBefore(member, studyUrl, post, id);
        studyPost.updateStudyPost(postUpdateRequestDto);
        saveFile(studyPost, files);

        studyPostRepository.save(studyPost);

        return PostResponseDto.builder()
                .title(studyPost.getTitle())
                .content(studyPost.getContent())
                .author(studyPost.getMember().getUsername())
                .files(studyPost.getFiles())
                .postEnum(studyPost.getPostEnum())
                .build();
    }

    //게시글 삭제
    @Override
    public ResponseEntity<?> deletePost(Member member, String post, String studyUrl, Long id) {
        studyPostBefore(member, studyUrl);
        // 1. 글이 있는지, 2. 작성자인지, 3, 파일이 존재하는지(존재하면 삭제 후 다시저장/ 반대는 그냥 초기화)
        StudyPost studyPost = existPost(id);
        checkAuthor(member, studyPost);
        deleteFile(studyPost);

        member.getStudyPosts().remove(studyPost);
        studyPostRepository.delete(studyPost);

        return new ResponseEntity<>("delete success", HttpStatus.OK);
    }

    //게시글 1개 조회
    @Override
    public PostInfoResponseDto getPostInfo(Long postId) {
        return new PostInfoResponseDto(studyPostRepository.findMemberById(postId).orElseThrow());
    }

    //게시물 페이징 처리
    @Override
    public PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        Page<StudyPost> searchResultStudyPost = studyPostRepository.search(postSearchCondition, pageable);
        return new PostPagingDto().postPagingDtoByStudyPost(searchResultStudyPost);
    }


    // curd 이전에 해야할 것
    private void studyPostBefore(Member member, String studyUrl) {
        checkExistMember(member); // 가입된 회원인지 임시 회원인지 확인
        checkExistStudyMember(member, checkExistStudy(studyUrl)); //스티디가 존재하는지 , 스터디 회원인지 확인
    }

    // 회원인지 확인
    private void checkExistMember(Member member) {
        Member studyMember = memberRepository.findById(member.getId()).orElseThrow(
                () -> new MemberNotFoundException("해당기능을 이용할 수 없습니다.")
        );
        checkTMPMember(studyMember);

    }

    // 임시 맴버인지 확인
    private void checkTMPMember(Member member) {
        if( member.getRole().toString().startsWith("TMP")){
            throw new AccessDeniedException("이메일 인증을 완료하세요");
        }
    }

    //스터디가 존재하는지 확인
    private Study checkExistStudy(String studyUrl) {
        Study study = studyRepository.findByUrl(studyUrl);
        if (study == null) throw new StudyNotFoundException("존재하지 않는 스터디 입니다.");
        return study;
    }

    // 스터디 맴버인지 확인
    private void checkExistStudyMember(Member member, Study study) {
        if (study.getTeacher() != member && study.getStudent() != member) {
            throw new AccessDeniedException("해당 기능을 이용할 수 없습니다.");
        }
    }

    private void validPostSaveRequestDto(PostSaveRequestDto postSaveRequestDto) {
        if (postSaveRequestDto.getTitle() == null || postSaveRequestDto.getContent() == null) {
            throw new IllegalArgumentException("제목 또는 내용을 작성해주세요");
        }
        if (postSaveRequestDto.getTitle().isEmpty() || postSaveRequestDto.getContent().isEmpty()) {
            throw new IllegalArgumentException("제목 또는 내용을 작성해주세요");
        }
    }


    private StudyPost createPostBefore(Member member, String studyUrl, String post, PostSaveRequestDto postSaveRequestDto) {
        validPostSaveRequestDto(postSaveRequestDto);
        StudyPost studyPost = postSaveRequestDto.studyPostToEntity();
        studyPost.setAuthor(member);
        studyPost.setStudy(checkExistStudy(studyUrl));
        studyPost.setPostEnum(post);

        return studyPost;
    }

    private void validPostUpdateRequestDto(PostUpdateRequestDto postUpdateRequestDto) {
        if (postUpdateRequestDto.getTitle() == null || postUpdateRequestDto.getContent() == null) {
            throw new IllegalArgumentException("제목 또는 내용을 작성해주세요");
        }
        if (postUpdateRequestDto.getTitle().isEmpty() || postUpdateRequestDto.getContent().isEmpty()) {
            throw new IllegalArgumentException("제목 또는 내용을 작성해주세요");
        }
    }

    private StudyPost updatePostBefore(Member member, String studyUrl, String post, Long id) {
        // 1. 글이 있는지, 2. 카테고리가 똑같은지, 3. 작성자인지, 4. 이미 파일이 존재하는지(존재하면 삭제 후 다시저장/ 반대는 그냥 초기화)
        StudyPost studyPost = existPost(id);
        checkAuthor(member, studyPost);
        checkCategory(post, studyPost);
        checkExistStudy(studyUrl);
        beforeStudyPostUpdateFileInit(studyPost);

        return studyPost;
    }

    private StudyPost existPost(Long id) {
        return  studyPostRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException("해당 게시물이 존재하지 않습니다."));
    }

    private void checkAuthor(Member member, StudyPost studyPost) {
        if (studyPost.getMember() != member)
            throw new AccessDeniedException("해당 기능을 이용하실 수 없습니다.");
    }

    private void checkCategory(String post, StudyPost studyPost) {
        if (!studyPost.getPostEnum().toString().equals("STUDY_" + post.toUpperCase()))
            throw new AccessDeniedException("잘못된 카테고리 입니다.");
    }


    // 파일 저장
    private void saveFile(StudyPost studyPost, List<MultipartFile> files) throws IOException {
        List<String> saveFilePath = fileService.save(files);
        for (String filePath : saveFilePath) {
            File file = new File();
            file.setTitle(filePath);
            file.setStudyPost(studyPost);
            studyPost.addFiles(file);

            fileRepository.save(file);
        }
    }

    // 업데이트 전제 파일이 존재하면 삭제하는 로직
    private void beforeStudyPostUpdateFileInit(StudyPost studyPost) {
        deleteFile(studyPost);
        studyPost.initFiles();
    }

    // 파일 삭제
    private void deleteFile(StudyPost studyPost) {
        if (!studyPost.getFiles().isEmpty()) {
            for (File filePath : studyPost.getFiles()) {
                fileService.delete(filePath.getFilePath());
                fileRepository.delete(filePath);
            }
        }
    }


}
