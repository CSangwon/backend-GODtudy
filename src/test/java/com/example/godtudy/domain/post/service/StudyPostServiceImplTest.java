package com.example.godtudy.domain.post.service;

import com.example.godtudy.domain.comment.repository.CommentRepository;
import com.example.godtudy.domain.comment.service.CommentService;
import com.example.godtudy.domain.post.repository.StudyPostRepository;
import com.example.godtudy.global.file.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudyPostServiceImplTest {

    @Autowired StudyPostService studyPostService;

    @Autowired CommentService commentService;

    @Autowired StudyPostRepository studyPostRepository;

    @Autowired CommentRepository commentRepository;

    @Autowired FileRepository fileRepository;


    private MockMultipartFile getMockUploadFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/file/GODtudy_logo_2.png");
        return new MockMultipartFile("file", "file.png", "image/png",
                new FileInputStream(resource.getFile().getAbsolutePath()));
    }

    private void deleteFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        file.delete();
    }


}