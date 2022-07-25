//package com.example.godtudy.domain.teacher.service;
//
//import com.example.godtudy.domain.member.entity.Member;
//import com.example.godtudy.domain.member.entity.QMember;
//import com.example.godtudy.domain.member.entity.QSubject;
//import com.example.godtudy.domain.member.entity.SubjectEnum;
//import com.example.godtudy.domain.member.repository.MemberRepository;
//import com.example.godtudy.domain.teacher.dto.request.TeacherSearchRequestDto;
//import com.example.godtudy.domain.teacher.dto.response.TeacherSearchResponseDto;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.core.QueryResults;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.EntityManager;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Service
//public class TeacherService {
//
//    private final MemberRepository memberRepository;
//    private final JPAQueryFactory queryFactory; // JPAQueryFactory 빈 주입
//
//
//    /**
//      */
//    public List<Member> allTeachers(TeacherSearchRequestDto request){
//
//        String type = request.getType();
//        String name = request.getName();
//        SubjectEnum subject = request.getSubject();
//
//        QMember qMember = QMember.member;
//        QSubject qSubject = QSubject.subject;
//
//}
