package com.example.godtudy.domain.study.repository;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    Study findByUrl(String url);

    List<Study> findAllByUrl(String url);

}
