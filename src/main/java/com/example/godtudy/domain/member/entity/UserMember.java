//package com.example.godtudy.domain.member.entity;
//
//import lombok.Getter;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.List;
//
//@Getter
//public class UserMember extends User {
//
//    private Member member;
//
//    public UserMember(Member member) {
//        super(member.getNickname(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_TEACHER"), new SimpleGrantedAuthority("ROLE_PARENTS"), new SimpleGrantedAuthority("ROLE_STUDENT")));
//        this.member = member;
//    }
//}
