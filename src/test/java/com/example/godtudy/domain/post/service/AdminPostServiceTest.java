package com.example.godtudy.domain.post.service;

import com.example.godtudy.WithMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Role;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.service.MemberService;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.AdminPostEnum;
import com.example.godtudy.domain.post.repository.AdminPostRepository;

import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.FileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class AdminPostServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    AdminPostService adminPostService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AdminPostRepository adminPostRepository;

    @Autowired
    FileRepository fileRepository;

    private MockMultipartFile getMockUploadFile() throws IOException {
        return new MockMultipartFile("file", "file.png", "image/png",
                new FileInputStream("/Users/forest_choi/Desktop/portfolio/GODtudy/Godtudy_File/test1.png"));
    }

    private void deleteFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        file.delete();
    }


    @Test
    @DisplayName("게시글 생성 성공 공지사항 - 파일 없음")
    @WithMember("swchoi1997")
    public void postCreateSuccessNotFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        //when
        adminPostService.createAdminPost(member, post, postSaveRequestDto);

        //then
        AdminPost test1 = adminPostRepository.findByTitle("test1").orElseThrow();

        assertThat(test1).isNotNull();
        assertThat(test1.getTitle()).isEqualTo("test1");
        assertThat(test1.getContent()).isEqualTo("test1");
        assertThat(test1.getMember().getUsername()).isEqualTo("swchoi1997");
        assertThat(test1.getFiles().isEmpty()).isTrue();
        assertThat(test1.getNoticeOrEvent()).isEqualTo(AdminPostEnum.valueOf(post.toUpperCase()));
    }


    @Test
    @DisplayName("게시글 생성 성공 공지사항 - 파일 있음")
    @WithMember("swchoi1997")
    public void postCreateSuccessExistFile() throws IOException{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        //when
        adminPostService.createAdminPost(member, multipartFiles, post, postSaveRequestDto);

        //then
        AdminPost test1 = adminPostRepository.findByTitle("test1").orElseThrow();

        System.out.println(test1.getFiles().get(1));

        assertThat(test1).isNotNull();
        assertThat(test1.getTitle()).isEqualTo("test1");
        assertThat(test1.getContent()).isEqualTo("test1");
        assertThat(test1.getMember().getUsername()).isEqualTo("swchoi1997");
        assertThat(test1.getNoticeOrEvent()).isEqualTo(AdminPostEnum.valueOf(post.toUpperCase()));


        File file = fileRepository.findById(test1.getFiles().get(0).getId()).orElseThrow();
        assertThat(test1.getFiles()).isNotEmpty();
        assertThat(file.getFilePath()).isEqualTo(test1.getFiles().get(0).getFilePath());

        deleteFile(file.getFilePath());
    }

    @Test
    @DisplayName("게시글 생성 실패 공지사항 - 권한 없음")
    @WithMember("swchoi1997")
    public void postCreateFailByNotAccess() throws IOException{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        member.setRole(Role.STUDENT);
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();


        //when //then
        Assertions.assertThrows(AccessDeniedException.class, () ->
                adminPostService.createAdminPost(member, post, postSaveRequestDto));
    }

    @Test
    @DisplayName("게시글 생성 실패 - 이름, 내용 없음")
    @WithMember("swchoi1997")
    public void postCreateFailNotExistPostNameOrContent() throws IOException{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto dtoNotContainTitle = PostSaveRequestDto.builder().content("test1").build();
        PostSaveRequestDto dtoNotContainContent = PostSaveRequestDto.builder().title("test1").build();

        //when //then
        Assertions.assertThrows(Exception.class, () ->
                adminPostService.createAdminPost(member, post, dtoNotContainTitle));
        Assertions.assertThrows(Exception.class, () ->
                adminPostService.createAdminPost(member, post, dtoNotContainContent));
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 없음 -> 없음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessNotExistFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        adminPostService.createAdminPost(member, post, postSaveRequestDto);

        //일단 게시물이 하나밖에 없다고 가정하고 title로 가져올 것 => id를 어떻게 가져와야할지 모르겠음 EntityManager사용해야하나...?
        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        adminPostService.updateAdminPost(member, post, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test2").orElseThrow();
        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test2");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());
        assertThat(updateAdminPost.getFiles()).isEmpty();
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 없음 -> 있음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessAddFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        adminPostService.createAdminPost(member, post, postSaveRequestDto);

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test1").content("test2").build();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        adminPostService.updateAdminPost(member, post, multipartFiles, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test1");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());

        File file = fileRepository.findById(updateAdminPost.getFiles().get(0).getId()).orElseThrow();
        assertThat(updateAdminPost.getFiles()).isNotEmpty();
        assertThat(file.getFilePath()).isEqualTo(updateAdminPost.getFiles().get(0).getFilePath());

        deleteFile(file.getFilePath());
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 있음 -> 없음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessMinusFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();

        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        adminPostService.createAdminPost(member, multipartFiles, post, postSaveRequestDto);

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();


        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();
        adminPostService.updateAdminPost(member, post, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test2").orElseThrow();
        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test2");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());
        assertThat(updateAdminPost.getFiles()).isEmpty();
    }

    @Test
    @DisplayName("게시글 수정 성공 - 파일 있음 -> 있음")
    @WithMember("swchoi1997")
    public void postUpdateSuccessChangeFile() throws Exception{
        //given
        Member member = memberRepository.findByUsername("swchoi1997").orElseThrow();
        String post = "notice";
        PostSaveRequestDto postSaveRequestDto = PostSaveRequestDto.builder().title("test1").content("test1").build();
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(getMockUploadFile());

        adminPostService.createAdminPost(member,multipartFiles, post, postSaveRequestDto);
        AdminPost adminPost = adminPostRepository.findByTitle("test1").orElseThrow();

        String initFilePath = adminPost.getFiles().get(0).getFilePath();

        //when
        PostUpdateRequestDto postUpdateRequestDto = PostUpdateRequestDto.builder().title("test2").content("test2").build();
        List<MultipartFile> multipartFiles2 = new ArrayList<>();
        multipartFiles2.add(getMockUploadFile());

        adminPostService.updateAdminPost(member, post, multipartFiles2, adminPost.getId(), postUpdateRequestDto);

        //then
        AdminPost updateAdminPost = adminPostRepository.findByTitle("test2").orElseThrow();

        assertThat(updateAdminPost.getId()).isEqualTo(adminPost.getId());
        assertThat(updateAdminPost.getTitle()).isEqualTo("test2");
        assertThat(updateAdminPost.getContent()).isEqualTo("test2");
        assertThat(updateAdminPost.getMember()).isEqualTo(adminPost.getMember());

        File file = fileRepository.findById(updateAdminPost.getFiles().get(0).getId()).orElseThrow();

        assertThat(updateAdminPost.getFiles()).isNotEmpty();
        assertThat(file.getFilePath()).isNotEqualTo(initFilePath);

        deleteFile(file.getFilePath());
    }






}