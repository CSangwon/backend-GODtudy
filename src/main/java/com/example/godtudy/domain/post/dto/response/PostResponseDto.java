package com.example.godtudy.domain.post.dto.response;

import com.example.godtudy.domain.post.entity.PostEnum;
import com.example.godtudy.global.file.File;

import java.util.List;

public class PostResponseDto {

    private String title;

    private String content;

    private String author;

    private List<File> files;

    private PostEnum postEnum;

    public PostResponseDto(){}

    public PostResponseDto(String title, String content, String author, List<File> files, PostEnum postEnum) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.files = files;
        this.postEnum = postEnum;
    }

    public static PostResponseDto.PostResponseDtoBuilder builder(){
        return new PostResponseDto.PostResponseDtoBuilder();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public PostEnum getPostEnum() {
        return postEnum;
    }

    public void setPostEnum(PostEnum postEnum) {
        this.postEnum = postEnum;
    }

    public static class PostResponseDtoBuilder {
        private String title;
        private String content;
        private String author;
        private List<File> files;
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
            this.files = files;
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
