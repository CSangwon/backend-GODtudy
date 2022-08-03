package com.example.godtudy.domain.member.entity;

import com.example.godtudy.domain.BaseEntity;
import com.example.godtudy.domain.comment.entity.Comment;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static javax.persistence.CascadeType.ALL;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birthday;

    private Boolean emailVerified;

    private String emailCheckToken; // 이메일 인증 토큰

    private LocalDateTime emailCheckTokenGeneratedAt;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImageUrl;

    private String bio;

    @Builder.Default
    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = ALL) //TODO 옵션 없앴을때 deleteById로 삭제해보기!!
    private List<Subject> subject = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = ALL)
    private List<AdminPost> adminPosts = new ArrayList<>(); // NullpointerException 7.5 커밋내용 보기

    @Builder.Default
    @OneToMany(mappedBy = "writer", orphanRemoval = true, cascade = ALL)
    private List<Comment> commentList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;



    public void setRole(Role role) {
        this.role = role;
    }

    // 이메일 체크 토근 랜덤한 값 생성
    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    // 이메일 인증 완료 시
    public void updateEmailVerified(boolean verified, LocalDateTime regDate){
        this.emailVerified = verified;
        this.createdDate = regDate;
    }

    //이메일 인증을 얼마나 자주 할 수 있을
    public boolean canSendConfirmEmail() {
//        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(1));
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    //프로필 업데이트
    public void updateProfile(ProfileRequestDto profileRequestDto) {
        this.nickname = profileRequestDto.getNickname();
        this.bio = profileRequestDto.getBio();
        this.profileImageUrl = profileRequestDto.getProfileImageUrl();
    }

    //비밀번호 업데이트
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
