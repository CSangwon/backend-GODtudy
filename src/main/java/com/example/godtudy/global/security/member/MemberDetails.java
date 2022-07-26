package com.example.godtudy.global.security.member;

import com.example.godtudy.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Builder
public class MemberDetails implements UserDetails {

    private Member member;

    public MemberDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(member.getRole().toString()));
        return roles;
    }

    public Long getId() {
        return member.getId();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }
    //계정 만료 여부 -> true(만료되지 않음)

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    //계정 잠김 여부 -> true(잠기지 않음)

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    //비밀번호 만료 여부 -> true(만료되지 않음)

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    //계정이 활성화 된건지 -> true(활성화)

    @Override
    public boolean isEnabled() {
        return true;
    }
}
