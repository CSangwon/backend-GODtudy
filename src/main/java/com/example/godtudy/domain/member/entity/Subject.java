package com.example.godtudy.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectEnum title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setTitle(SubjectEnum title) {
        this.title = title;
    }

    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getSubject().remove(this);
        }
        this.member = member;
        member.getSubject().add(this);
    }

    public static Subject createMemberSubject(Member member, SubjectEnum title) {
        Subject subject = new Subject();
        subject.setMember(member);
        subject.setTitle(title);
        member.addSubject(subject);
        return subject;
    }
}
