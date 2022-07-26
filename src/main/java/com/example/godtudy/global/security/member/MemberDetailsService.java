package com.example.godtudy.global.security.member;

import com.example.godtudy.domain.member.entity.Member;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.global.advice.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
//데이터베이스에서 유저정보를 가져옴
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
        log.info(member.getRole().toString());

//        List<GrantedAuthority> roles = new ArrayList<>();
//        roles.add(new SimpleGrantedAuthority(member.getRole().toString()));
//        log.info("----------------------------------");
//        log.info(roles.toString());

//        return MemberDetails.builder()
//                .username(member.getUsername())
//                .password(member.getPassword())
//                .authorities(roles)
//                .build();


        return new MemberDetails(member);
    }


}
