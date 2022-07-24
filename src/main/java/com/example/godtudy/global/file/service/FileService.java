package com.example.godtudy.global.file.service;


import com.example.godtudy.global.file.exception.FileException;
import com.example.godtudy.global.file.exception.FileExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${spring.servlet.multipart.location}")
    private String fileDir;

    private List<String> filePaths = new ArrayList<>();

    public List<String> save(MultipartFile multipartFile) throws IOException {
        String filePath = fileDir + UUID.randomUUID() + multipartFile.getOriginalFilename();
        multipartFile.transferTo(new File(filePath));
        filePaths.add(filePath);

        return filePaths;
    }

    public List<String> save(List<MultipartFile> multipartFiles) throws IOException {

        for (MultipartFile file : multipartFiles) {
            String filePath = fileDir + "/" +  UUID.randomUUID() + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            filePaths.add(filePath);
        }
        return filePaths;
    }

    public void delete(String filePath) {
        File file = new File(filePath);

        if(!file.exists()) return;

        if(!file.delete()) throw new FileException(FileExceptionType.FILE_CAN_NOT_DELETE);
    }

}
