package com.example.godtudy.domain.post.controller;

import com.example.godtudy.domain.member.entity.CurrentMember;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostSaveRequestDto;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.post.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminPostApiController {

    private final AdminPostService adminPostService;
//
//    @PostMapping("{post}/new")
//    public ResponseEntity<?> createNoticeEvent(@PathVariable String post ,@CurrentMember Member member,
//                                               @RequestBody PostSaveRequestDto postSaveRequestDto) {
//
//        return adminPostService.createAdminPost(member, post, postSaveRequestDto);
//    }

    @SneakyThrows
    @PostMapping("{post}/new")
    public ResponseEntity<?> createNoticeEvent(@PathVariable String post ,@CurrentMember Member member,
                                               @RequestBody PostSaveRequestDto postSaveRequestDto,
                                               @RequestPart List<MultipartFile> files) {
        if (files.isEmpty()) {
            return adminPostService.createAdminPost(member, post, postSaveRequestDto);
        }
        return adminPostService.createAdminPost(member,files, post, postSaveRequestDto);
    }

    @PostMapping("/{post}/{id}")
    public ResponseEntity<?> updateNoticeEvent(@PathVariable String post, @PathVariable Long id, @CurrentMember Member member,
                                               @RequestBody PostUpdateRequestDto postUpdateRequestDto,
                                               @RequestPart List<MultipartFile> files) throws IOException {
        if (files.isEmpty()) {
            return adminPostService.updateAdminPost(member, post, id , postUpdateRequestDto);
        }
        return adminPostService.updateAdminPost(member, post,files, id , postUpdateRequestDto);

    }

    @DeleteMapping("/{post}/{id}")
    public ResponseEntity<?> deleteNoticeEvent(@PathVariable String post, @PathVariable Long id, @CurrentMember Member member) {
        return adminPostService.deleteAdminPost(member, id);
    }
}
