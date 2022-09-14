package com.example.godtudy.domain.post.entity;

import com.example.godtudy.domain.BaseEntity;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.dto.request.PostUpdateRequestDto;
import com.example.godtudy.domain.study.entity.Study;
import com.example.godtudy.global.file.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPost extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "studyPost")
    private List<File> files = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostEnum postEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    @Builder.Default
    @OneToMany(mappedBy = "adminPost", orphanRemoval = true, cascade = ALL) //, orphanRemoval = true, cascade = ALL
    @OrderBy("id asc")
    private List<Comment> commentList = new ArrayList<>();

    // == 연관관계 편의 메서드 == //
    public void setAuthor(Member member) {
        if (this.member != null) {
            this.member.getStudyPosts().remove(this);
        }
        this.member = member;
        member.getStudyPosts().add(this);
    }

    public void setStudy(Study study) {
        if (this.study != null) {
            this.study.getStudyPosts().remove(this);
        }
        this.study = study;
        study.getStudyPosts().add(this);
    }

    public void addFiles(File file) {
        this.files.add(file);
        if (file.getStudyPost()!= this) {
            file.setStudyPost(this);
        }
    }

    public void setPostEnum(String post) {
        this.postEnum = PostEnum.valueOf("STUDY_" + post.toUpperCase());
    }

    public void initFiles() {
        this.files = new ArrayList<>();
    }

    public void updateStudyPost(PostUpdateRequestDto postUpdateRequestDto) {
        this.title = postUpdateRequestDto.getTitle();
        this.content = postUpdateRequestDto.getContent();
    }
}
