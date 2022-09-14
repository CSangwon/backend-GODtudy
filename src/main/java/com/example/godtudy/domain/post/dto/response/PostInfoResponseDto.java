package com.example.godtudy.domain.post.dto.response;

import com.example.godtudy.domain.comment.dto.response.CommentInfoResponseDto;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.post.entity.AdminPost;
import com.example.godtudy.domain.post.entity.StudyPost;
import com.example.godtudy.global.file.File;
import com.example.godtudy.global.file.dto.FileResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostInfoResponseDto {

    private Long postId;

    private String title;

    private String content;

    private String author;

    private List<FileResponseDto> files = new ArrayList<>();

    private List<CommentInfoResponseDto> commentInfoResponseDtoList = new ArrayList<>();

    public PostInfoResponseDto(AdminPost adminPost) {
        this.postId = adminPost.getId();
        this.title = adminPost.getTitle();
        this.content = adminPost.getContent();
        this.author = adminPost.getMember().getNickname();
        this.commentInfoResponseDtoList = parentChild(adminPost.getCommentList());
        for (File file : adminPost.getFiles()) {
            FileResponseDto fileResponseDto = FileResponseDto.builder().fileId(file.getId()).title(file.getFilePath()).build();
            this.files.add(fileResponseDto);
        }
    }

    public PostInfoResponseDto(StudyPost studyPost) {
        this.postId = studyPost.getId();
        this.title = studyPost.getTitle();
        this.content = studyPost.getContent();
        this.author = studyPost.getMember().getNickname();
        this.commentInfoResponseDtoList = parentChild(studyPost.getCommentList());
        for (File file : studyPost.getFiles()) {
            FileResponseDto fileResponseDto = FileResponseDto.builder().fileId(file.getId()).title(file.getFilePath()).build();
            this.files.add(fileResponseDto);
        }
    }

    public List<CommentInfoResponseDto> parentChild(List<Comment> commentList) {
        Map<Comment, List<Comment>> parentChildMap = commentList.stream()
                .filter(comment -> comment.getParentComment() != null) // filter 로 대댓글인 것만 가져옴
                .collect(Collectors.groupingBy(Comment::getParentComment)); // 그룹화를 해줌 parent가 같은걸로!!

        List<CommentInfoResponseDto> comments = new ArrayList<>();
        for (Comment key : parentChildMap.keySet()) {
            comments.add(new CommentInfoResponseDto(key, parentChildMap.get(key)));
        }
        return comments;
    }



}
