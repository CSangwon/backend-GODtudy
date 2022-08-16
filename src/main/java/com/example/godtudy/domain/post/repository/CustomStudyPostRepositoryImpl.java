package com.example.godtudy.domain.post.repository;

import com.example.godtudy.domain.member.entity.QMember;
import com.example.godtudy.domain.post.dto.request.PostSearchCondition;
import com.example.godtudy.domain.post.entity.QStudyPost;
import com.example.godtudy.domain.post.entity.StudyPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CustomStudyPostRepositoryImpl implements CustomStudyPostRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public CustomStudyPostRepositoryImpl(EntityManager em) {
        jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<StudyPost> search(PostSearchCondition postSearchCondition, Pageable pageable) {

        //fetch를 통해 컨텐츠를 가져옴
        List<StudyPost> content = jpaQueryFactory.selectFrom(QStudyPost.studyPost)
                .where(
                        titleHasStr(postSearchCondition.getTitle()),
                        contentHasStr(postSearchCondition.getContent())
                )
                .leftJoin(QStudyPost.studyPost.member, QMember.member)

                .fetchJoin()
                .orderBy(QStudyPost.studyPost.createdDate.desc()) // 최신날짜
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count만 가져오는 쿼리
        JPAQuery<StudyPost> countQuery = jpaQueryFactory.selectFrom(QStudyPost.studyPost)
                .where(
                        titleHasStr(postSearchCondition.getTitle()),
                        contentHasStr(postSearchCondition.getContent())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
        // 조회된 컨텐츠(content), 요청으로부터 가져온 페이지 요청 데이터(pageable)
        //마지막 인자로 함수를 전달하는데, 내부 작동에 의해서 토탈카운트가 페이지 사이즈보다 적거나, 마지막 페이지 일 경우 해당 함수를 실행하지 않는다. => 쿼리를 조금 줄일 수 있음
        //마지막에 fetch().size()를 통해서 관리되고 있다.


    }

    private BooleanExpression titleHasStr(String title) {
        return StringUtils.hasLength(title) ? QStudyPost.studyPost.content.contains(title) : null;
        //메소드 위임 기능을 이용하여 쿼리 타입에 검색조건을 직접 정의할 수 있음
    }

    private BooleanExpression contentHasStr(String content) {
        return StringUtils.hasLength(content) ? QStudyPost.studyPost.content.contains(content) : null;
    }
}
