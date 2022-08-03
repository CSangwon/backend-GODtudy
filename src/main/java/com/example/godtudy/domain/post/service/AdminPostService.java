package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.dto.response.PostInfoResponseDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.AdminPostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;
import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.FileRepository;
import com.example.godtudy.global.file.service.FileService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 게시물 등록
     */

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

        if (!adminPost.getFiles().isEmpty()) {
            for (File filePath : adminPost.getFiles()) {
                fileService.delete(filePath.getFilePath());
                fileRepository.delete(filePath);
            }
            adminPost.initFiles();
        }

        adminPostRepository.save(adminPost);

        return new ResponseEntity<>("Notice Update", HttpStatus.OK);

    }
    // 게시물 수정 - 파일 있음
    @Override
    public ResponseEntity<?> updatePost(Member member, String post, List<MultipartFile> files,
                                             Long id, PostUpdateRequestDto postUpdateRequestDto) throws IOException {
        AdminPost adminPost = updateAdminPostBefore(member, id, post);
        adminPost.updateAdminPost(postUpdateRequestDto);

        if (!adminPost.getFiles().isEmpty()) {
            for (File filePath : adminPost.getFiles()) {
                fileService.delete(filePath.getFilePath());
                fileRepository.delete(filePath);
            }
            adminPost.initFiles();
        }

        saveFile(adminPost, files);
        adminPostRepository.save(adminPost);
        return new ResponseEntity<>("Notice Update", HttpStatus.OK);
    }

    /**
     * 게시물 삭제
     */
    @Override
    public ResponseEntity<?> deletePost(Member member, Long id) {
        AdminPost adminPost = adminPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        checkIfAdmin(member);
        checkAuthor(member, adminPost);

        if (!adminPost.getFiles().isEmpty()) {
            for (File filePath : adminPost.getFiles()) {
                fileService.delete(filePath.getFilePath());
                fileRepository.delete(filePath);
            }
        }
        member.getAdminPosts().remove(adminPost);
        adminPostRepository.deleteById(adminPost.getId());


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

    //TODO 페이징해서 게시글 가져오는거 구현해야하고 파일 업로드하는거랑 댓글 기능까지 작성해야함, 테스트코드도 작성해야함



    /**
     * 관리자인지 확인
     */
    private void checkIfAdmin(Member member) {
        Member checkMember = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        if (!checkMember.getRole().equals(Role.ADMIN) ) {
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

    private AdminPost createAdminPostBefore(Member member, String post, PostSaveRequestDto postSaveRequestDto) {
        checkIfAdmin(member); //관리자인지 확인
        AdminPost adminPost = postSaveRequestDto.toEntity();
        adminPost.setAuthor(member); // 현재 맴버 매핑
        adminPost.setAdminPostEnum(post); // 현재 게시판 작성

        return adminPost;
    }

    private AdminPost updateAdminPostBefore(Member member, Long id, String post) {
        AdminPost adminPost = adminPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
        checkIfAdmin(member); //관리자 인지 확인
        checkAuthor(member, adminPost); // 작성자 인지 확인
        checkCategory(adminPost, post); // 공지사항 <-> 이벤트 가 바뀌지 않았는지 확인

        return adminPost;
    }

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




}
