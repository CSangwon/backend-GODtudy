package com.example.godtudy.global.file;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.entity.Subject;
import com.example.godtudy.domain.member.entity.SubjectEnum;
import com.example.godtudy.domain.post.entity.AdminPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filePath;

    @ManyToOne(fetch = FetchType.EAGER)
    private AdminPost adminPost;

    public void setAdminPost(AdminPost adminPost) {
        if (this.adminPost != null) {
            this.adminPost.getFiles().remove(this);
        }
        this.adminPost = adminPost;
        adminPost.getFiles().add(this);
    }

    public void setTitle(String filePath) {
        this.filePath = filePath;
    }
}
