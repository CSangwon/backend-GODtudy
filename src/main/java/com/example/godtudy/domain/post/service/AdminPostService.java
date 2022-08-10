package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.dto.response.PostPagingDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.AdminPostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminPostService implements PostService{

    private final AdminPostRepository adminPostRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    // 게시물 저장 - 파일 없음
    @Override
    public ResponseEntity<?> createPost(Member member, String post, PostSaveRequestDto postSaveRequestDto) {
        AdminPost adminPost = createAdminPostBefore(member, post, postSaveRequestDto);
        adminPostRepository.save(adminPost);

        return new ResponseEntity<>("Notice Create", HttpStatus.OK);
    }
    // 게시물 저장 - 파일 있음
    @Override
    public ResponseEntity<?> createPost(Member member, List<MultipartFile> files,
                                             String post, PostSaveRequestDto postSaveRequestDto) throws IOException {
        AdminPost adminPost = createAdminPostBefore(member, post, postSaveRequestDto);
        //file 저장
        saveFile(adminPost, files);
        adminPostRepository.save(adminPost);

        return new ResponseEntity<>("Notice Create", HttpStatus.OK);
    }

    /**
     * 게시물 수정
     */
    // 게시물 수정 - 파일 없음
    @Override
    public ResponseEntity<?> updatePost(Member member, String post, Long id, PostUpdateRequestDto postUpdateRequestDto){
        AdminPost adminPost = updateAdminPostBefore(member, id, post);
        adminPost.updateAdminPost(postUpdateRequestDto);
        beforeAdminPostUpdateFileInit(adminPost);

        adminPostRepository.save(adminPost);

        return new ResponseEntity<>("Notice Update", HttpStatus.OK);

    }
    // 게시물 수정 - 파일 있음
    @Override
    public ResponseEntity<?> updatePost(Member member, String post, List<MultipartFile> files,
                                             Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException {
        AdminPost adminPost = updateAdminPostBefore(member, id, post);
        adminPost.updateAdminPost(postUpdateRequestDto);
        beforeAdminPostUpdateFileInit(adminPost);

        saveFile(adminPost, files);
        adminPostRepository.save(adminPost);
        return new ResponseEntity<>("Notice Update", HttpStatus.OK);
    }

    /**
     * 게시물 삭제
     */
    @Override
    public ResponseEntity<?> deletePost(Member member, String post, Long id) {
        AdminPost adminPost = deleteAdminPostBefore(member, post, id);
        deleteFile(adminPost);

        member.getAdminPosts().remove(adminPost);
        adminPostRepository.delete(adminPost);

        return new ResponseEntity<>("Notice Delete", HttpStatus.OK);
    }

    /**
     * 게시물 1개 조회
     */
    @Override
    public PostInfoResponseDto getPostInfo(Long postId) {

        AdminPost adminPost = adminPostRepository.findAuthorById(postId).orElseThrow();
        return new PostInfoResponseDto(adminPost);
    }

    /**
     * 게시물 페이징 처리
     */
    @Override
    public PostPagingDto getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        Page<AdminPost> searchResultAdminPost = adminPostRepository.search(postSearchCondition, pageable);
        return new PostPagingDto().postPagingDtoByAdminPost(searchResultAdminPost);

    }

    /**
     * 존재하는 회원인지 확인
     */
    private void checkMemberExist(Member member) {
        memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
    }

    /**
     * 관리자인지 확인
     */
    private void checkIfAdmin(Member member) {
        if (!member.getRole().equals(Role.ADMIN) ) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    /**
     * 이 게시글의 작성자인지 확인
     */
    private void checkAuthor(Member member, AdminPost adminPost) {
        if (!adminPost.getMember().getUsername().equals(member.getUsername())) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkCategory(AdminPost adminPost, String post) {
        if (!adminPost.getNoticeOrEvent().equals(AdminPostEnum.valueOf(post.toUpperCase()))) {
            throw new AccessDeniedException("잘못된 카테고리 입니다.");
        }
    }

    /**
     * adminpostService실행 전
     * 존재하는 맴버인지, 관리자인지 확인
     */
    private void adminPostBefore(Member member) {
        checkMemberExist(member); // 존재하는 맴버인지 확인
        checkIfAdmin(member); //관리자인지 확인
    }

    /**
     * adminpost 생전 전 확인해야할 것들
     */
    private AdminPost createAdminPostBefore(Member member, String post, PostSaveRequestDto postSaveRequestDto) {
        adminPostBefore(member);
        AdminPost adminPost = postSaveRequestDto.toEntity();
        adminPost.setAuthor(member); // 현재 맴버 매핑
        adminPost.setAdminPostEnum(post); // 현재 게시판 작성

        return adminPost;
    }

    /**
     * adminpost 수정 전 확인해야할 것들
     */
    private AdminPost updateAdminPostBefore(Member member, Long id, String post) {
        adminPostBefore(member);
        AdminPost adminPost = adminPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        checkAuthor(member, adminPost); // 작성자 인지 확인
        checkCategory(adminPost, post); // 공지사항 <-> 이벤트 가 바뀌지 않았는지 확인

        return adminPost;
    }

    /**
     * adminpost 삭제 전 확인해야할 것들
     */
    private AdminPost deleteAdminPostBefore(Member member, String post, Long id) {
        adminPostBefore(member);
        AdminPost adminPost = adminPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        checkAuthor(member, adminPost);

        return adminPost;
    }

    /**
     * file 저장
     */
    private void saveFile(AdminPost adminPost, List<MultipartFile> files) throws IOException {
        List<String> saveFilesPath = fileService.save(files);
        for (String filePath : saveFilesPath) {
            File file = new File();
            file.setTitle(filePath);
            file.setAdminPost(adminPost);
            adminPost.addFiles(file);

            fileRepository.save(file);
        }
    }

    /**
     * 게시글 수정 전 파일이 있으면 초기화함
     */
    private void beforeAdminPostUpdateFileInit(AdminPost adminPost){
        deleteFile(adminPost);
        adminPost.initFiles();
    }

    /**
     *  파일 삭제
     */
    private void deleteFile(AdminPost adminPost) {
        if (!adminPost.getFiles().isEmpty()) {
            for (File filePath : adminPost.getFiles()) {
                fileService.delete(filePath.getFilePath());
                fileRepository.delete(filePath);
            }
        }
    }




}
