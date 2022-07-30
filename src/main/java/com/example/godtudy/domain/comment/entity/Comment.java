package com.example.godtudy.domain.comment.entity;

import com.example.godtudy.domain.BaseEntity;
import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.post.entity.AdminPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private AdminPost adminPost;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    private List<Comment> childComment = new ArrayList<>();

    private Boolean isFirstComment = false;


    //연관관계 편의 메서드
    public void setAdminPost(AdminPost adminPost) {
        if (this.adminPost != null) {
            this.adminPost.getCommentList().remove(this);
        }
        this.adminPost = adminPost;
        adminPost.getCommentList().add(this);
    }

    public void setWriter(Member member) {
        if (this.writer != null) {
            this.writer.getCommentList().remove(this);
        }
        this.writer = member;
        writer.getCommentList().add(this);
    }

    public void setIsFirstComment(){
        this.isFirstComment = true;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
        parentComment.addChildComment(this);
    }

    public void addChildComment(Comment childComment) {
        this.childComment.add(childComment);
    }

    public void updateComment(String content) {
        this.content = content;
    }


    public void removeParentCommentExistChildComment() {
        this.content = null;
        this.writer = null;
    }

    public void removeCommentAll() {

    }

}
