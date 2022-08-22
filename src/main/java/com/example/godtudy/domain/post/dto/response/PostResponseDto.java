package com.example.godtudy.domain.post.dto.response;

import com.example.godtudy.domain.post.entity.PostEnum;
import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.dto.FileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostResponseDto {

    private String title;

    private String content;

    private String author;

//    private List<File> files;
    private List<FileResponseDto> files = new ArrayList<>();

    private PostEnum postEnum;


    public PostResponseDto(String title, String content, String author, List<FileResponseDto> files, PostEnum postEnum) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.postEnum = postEnum;
        for (FileResponseDto file : files) {
            this.files.add(file);
        }
    }

    public static PostResponseDto.PostResponseDtoBuilder builder(){
        return new PostResponseDto.PostResponseDtoBuilder();
    }

    public static class PostResponseDtoBuilder {
        private String title;
        private String content;
        private String author;
        private List<FileResponseDto> files;
        private PostEnum postEnum;

        public PostResponseDtoBuilder() {
        }

        public PostResponseDto.PostResponseDtoBuilder title(final String title) {
            this.title = title;
            return this;
        }
        public PostResponseDto.PostResponseDtoBuilder content(final String content) {
            this.content = content;
            return this;
        }
        public PostResponseDto.PostResponseDtoBuilder author(final String author) {
            this.author = author;
            return this;
        }
        public PostResponseDto.PostResponseDtoBuilder files(final List<File> files) {
            List<FileResponseDto> filesDto = new ArrayList<>();
            for (File file : files) {
                filesDto.add(FileResponseDto.builder().fileId(file.getId()).title(file.getFilePath()).build());
            }
            this.files = filesDto;
            return this;
        }

        public PostResponseDto.PostResponseDtoBuilder postEnum(final PostEnum postEnum) {
            this.postEnum = postEnum;
            return this;
        }

        public PostResponseDto build() {
            return new PostResponseDto(this.title, this.content, this.author, this.files, this.postEnum);
        }
    }

}
