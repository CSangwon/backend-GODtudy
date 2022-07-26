package com.example.godtudy.domain.member.entity;

import com.example.godtudy.domain.BaseEntity;
import com.example.godtudy.domain.member.dto.request.profile.ProfileRequestDto;
import com.example.godtudy.domain.post.entity.AdminPost;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Entity
@Getter
@EqualsAndHashCode(of = "id")
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

    @OneToMany(mappedBy = "member")
//    private Set<Subject> subject = new HashSet<>();
    private List<Subject> subject = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<AdminPost> adminPosts = new ArrayList<>(); // NullpointerException 7.5 커밋내용 보기

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    // == 연관관계 편의 메서드 ==//
    public void addSubject(Subject subject) {
        this.subject.add(subject);
        if (subject.getMember() != this) {
            subject.setMember(this);
        }
    }

    public void addAdminPost(AdminPost adminPost) {
        this.adminPosts.add(adminPost);
        if (adminPost.getMember() != this) {
            adminPost.setAuthor(this);
        }
    }

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
