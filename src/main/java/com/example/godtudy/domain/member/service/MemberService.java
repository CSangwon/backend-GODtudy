package com.example.godtudy.domain.member.service;

import com.example.godtudy.domain.mail.EmailMessage;
import com.example.godtudy.domain.mail.EmailService;
import com.example.godtudy.domain.member.dto.request.*;
import com.example.godtudy.domain.member.dto.request.profile.FindPasswordRequestDto;
import com.example.godtudy.domain.member.dto.request.profile.FindUsernameRequestDto;
import com.example.godtudy.domain.member.dto.response.profile.FindUsernameResponseDto;
import com.example.godtudy.domain.member.dto.response.JwtTokenResponseDto;
import com.example.godtudy.domain.member.dto.response.MemberLoginResponseDto;
import com.example.godtudy.domain.member.entity.*;
import com.example.godtudy.domain.member.redis.RedisKey;
import com.example.godtudy.domain.member.redis.RedisService;
import com.example.godtudy.domain.member.repository.MemberRepository;
import com.example.godtudy.domain.member.repository.SubjectRepository;
import com.example.godtudy.global.advice.exception.*;
import com.example.godtudy.global.config.AppProperties;
import com.example.godtudy.global.security.jwt.JwtTokenProvider;
import com.example.godtudy.global.security.member.MemberDetails;
import com.example.godtudy.global.security.member.MemberDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final EmailService emailService;
    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final SubjectRepository subjectRepository;


    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDetailsService memberDetailsService;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(MemberLoginRequestDto memberLoginRequestDto) {
        if(memberRepository.findByUsername(memberLoginRequestDto.getUsername()).isEmpty()){
            return new ResponseEntity<>("해당하는 맴버가 없습니다.", HttpStatus.BAD_REQUEST);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberLoginRequestDto.getUsername(), memberLoginRequestDto.getPassword()));

        String accessToken = jwtTokenProvider.createAccessToken(memberLoginRequestDto.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(memberLoginRequestDto.getUsername());
        redisService.setDataWithExpiration(RedisKey.REFRESH.getKey() + memberLoginRequestDto.getUsername(),
                refreshToken, JwtTokenProvider.REFRESH_TOKEN_VALID_TIME);

        final MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername(memberLoginRequestDto.getUsername());

        return ResponseEntity.ok(new MemberLoginResponseDto(memberDetails.getId(), memberLoginRequestDto.getUsername(), accessToken, refreshToken));
    }

    public ResponseEntity<?> reissueAuthenticationToken(TokenRequestDto tokenRequestDto) {
        // 사용자로부터 받은 Refresh Token 유효성 검사
        // Refresh Token 마저 만료되면 다시 로그인
        if (jwtTokenProvider.isTokenExpired(tokenRequestDto.getRefreshToken())
                || !jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new IllegalArgumentException("잘못된 요청입니다. 다시 로그인해주세요.");
        }
        // Access Token 에 기술된 사용자 이름 가져오기
        String username = jwtTokenProvider.getUsernameFromToken(tokenRequestDto.getAccessToken());
        String refreshTokenByRedis = redisService.getData(RedisKey.REFRESH.getKey() + username);

        if (refreshTokenByRedis == null || !refreshTokenByRedis.equals(tokenRequestDto.getRefreshToken())) {
            throw new InvalidRefreshTokenException();
        }
        Member member = memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);

        // 새로운 Access Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getUsername());

        return ResponseEntity.ok(new JwtTokenResponseDto(accessToken, refreshTokenByRedis));
    }

    // 로그아웃 ->  refresh토근제거, accesstoken blacklist생성
    public void logout(String username, String accessToken) {
        redisService.deleteData(RedisKey.REFRESH.getKey() + username);
        redisService.setDataWithExpiration("logout" + accessToken.substring(7), accessToken.substring(7),
                jwtTokenProvider.getExpirationDateFromToken(accessToken.substring(7)).getTime() / 1000000);
    }

    /*  회원가입  */
    public Member initJoinMember(MemberJoinForm memberJoinForm, String role) {
        String tmpRole = "";
        String role1 = role.toUpperCase();
        if (!role1.equals("ADMIN")){ tmpRole = "TMP_" + role1;}
        else{ tmpRole = role1; }

        //TODO 회원가입하고 이메일인증을 하지 않았을 때 같은 이메일로 회원가입이 들어오면 어떻게 해야할가를 생각
        memberJoinForm.setPassword(passwordEncoder.encode(memberJoinForm.getPassword()));
        memberJoinForm.setRole(Role.valueOf(tmpRole));
        Member newMember = memberJoinForm.toEntity();

        addSubject(newMember, memberJoinForm); //   과목 명 받아서 저장
        newMember.generateEmailCheckToken(); //이메일 인증 토큰 값 생성

        return memberRepository.save(newMember);
    }

    /*   과목 명 받아서 저장   */

    public void addSubject(Member newMember, MemberJoinForm memberJoinForm) {
        if (memberJoinForm.getSubject().isEmpty()) {
            throw new IllegalArgumentException("입력하지 않은 부분이 있습니다. 확인해 주세요.");
        }
        for (String sub : memberJoinForm.getSubject()) {
            SubjectEnum title = SubjectEnum.valueOf(sub.toUpperCase(Locale.ROOT));
            Subject subject = Subject.createMemberSubject(newMember, title);
            subjectRepository.save(subject);
        }
    }

    private ResponseEntity<?> completeJoinMember(Member member) {
        member.updateEmailVerified(true, LocalDateTime.now());
        if (member.getRole() == Role.TMP_PARENTS) {
            member.setRole(Role.PARENTS);
        } else if (member.getRole() == Role.TMP_STUDENT) {
            member.setRole(Role.STUDENT);
        } else {
            member.setRole(Role.TEACHER);
        }
        return new ResponseEntity<>("Join Success Final", HttpStatus.OK);
    }

    /* 이메일 보내기 - 회원가입 */
    public void sendJoinConfirmEmail(Member member) {
        Context context = new Context();
        context.setVariable("link", "/api/member/checkEmailToken/" + member.getEmailCheckToken() + "/" + member.getEmail());
        context.setVariable("username", member.getUsername());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "GODtudy 서비스를 사용하려면 아래 링크를 클릭하세요!");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(member.getEmail())
                .subject("GODtudy, 회원가입 인증")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    /*  이메일로 보낸 토큰 확인   */
    public ResponseEntity<?> checkEmailToken(String token, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 이메일입니다."));
        if(member.getEmailVerified()) {
            return new ResponseEntity<>("이미 인증된 회원입니다.", HttpStatus.BAD_REQUEST);
        }
        //이메일 토큰이 같은지
        if (!member.isValidToken(token)) {
            return new ResponseEntity<>("유요하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST);
        }

        return completeJoinMember(member);
    }

    //아이디 중복확인
    @Transactional(readOnly = true)
    public void usernameCheckDuplication(UsernameRequestDto usernameRequestDto) {
        memberRepository.findByUsername(usernameRequestDto.getUsername())
                .ifPresent(e -> {
                    throw new MemberUsernameAlreadyExistsException("이미 사용중인 아이디 입니다.");
                });
    }

    // 인증된 이메일 Or 이메일 중복확인
    @Transactional(readOnly = true)
    public void emailCheckDuplication(EmailRequestDto emailRequestDto) {
        memberRepository.findByEmail(emailRequestDto.getEmail())
                .ifPresent(e -> {
                    throw new MemberEmailAlreadyExistsException("이미 등록된 이메일 입니다.");
                });
    }

    //닉네임 중복확인
    @Transactional(readOnly = true)
    public void nicknameCheckDuplication(NicknameRequestDto nicknameRequestDto) {
        memberRepository.findByNickname(nicknameRequestDto.getNickname())
                .ifPresent(e -> {
                    throw new MemberNicknameAlreadyExistsException("이미 사용중인 닉네임 입니다.");
                });
    }

    //아이디 찾기
    public FindUsernameResponseDto findUsername(FindUsernameRequestDto findUsernameRequestDto) {
        // 회원이 존재하는지 확인
        if (!memberRepository.existsByName(findUsernameRequestDto.getName()) || !memberRepository.existsByEmail(findUsernameRequestDto.getEmail())) {
            throw new MemberNotFoundException("존재하지 않는 회원정보입니다.");
        }
        //TODO 이메일을 인증을 해야할지
        Member member = memberRepository.findByEmail(findUsernameRequestDto.getEmail()).orElseThrow(MemberNotFoundException::new);
        return FindUsernameResponseDto.builder().username(member.getUsername()).build();
    }

    //비밀번호 찾기
    public ResponseEntity<?> findPassword(FindPasswordRequestDto findPasswordRequestDto) {
        // 1. 회원이 존재하는지 확인
        if (!memberRepository.existsByName(findPasswordRequestDto.getName())
                || !memberRepository.existsByUsername(findPasswordRequestDto.getUsername())
                || !memberRepository.existsByEmail(findPasswordRequestDto.getEmail())) {
            throw new MemberNotFoundException("존재하지 않는 회원정보입니다.");
        }

        //2. 존재하는걸로 판명나면 임시비빌번호 생성
        Member member = memberRepository.findByUsername(findPasswordRequestDto.getUsername()).orElseThrow();
        String tmpPassword = UUID.randomUUID().toString();
        //3. 임시 비밀번호로 비빌번호 변경
        member.updatePassword(tmpPassword.substring(0, 6));
        memberRepository.save(member);

        //4. 이메일 보내기
        sendPasswordConfirmEmail(member);

        return new ResponseEntity<>("임시 비밀번호를 이메일로 보냈습니다.", HttpStatus.OK);
    }

    /* 이메일 보내기 - 비밀번호 찾기 */
    public void sendPasswordConfirmEmail(Member member) {
        Context context = new Context();
        context.setVariable("tmpPassword", member.getPassword());
        context.setVariable("linkName", "GODtudy 임시 비밀번호 ");
        context.setVariable("message", "GODtudy 임시 비밀번호 가 발급되었습니다. 아래 비밀번호로 로그인을 진행하세요");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/tmpPassword", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(member.getEmail())
                .subject("GODtudy, 임시비밀번호 발급")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }


}
