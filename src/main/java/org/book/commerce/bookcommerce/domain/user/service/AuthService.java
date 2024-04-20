package org.book.commerce.bookcommerce.domain.user.service;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.bookcommerce.common.util.AESUtil;
import org.book.commerce.bookcommerce.common.util.RedisUtil;
import org.book.commerce.bookcommerce.common.security.JwtTokenProvider;
import org.book.commerce.bookcommerce.common.dto.CommonResponseDto;
import org.book.commerce.bookcommerce.domain.user.dto.LoginInfo;
import org.book.commerce.bookcommerce.domain.user.dto.SignupInfo;
import org.book.commerce.bookcommerce.domain.user.repository.UsersRepository;
import org.book.commerce.bookcommerce.domain.user.domain.Role;
import org.book.commerce.bookcommerce.domain.user.domain.Users;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;
    private final MailService mailService;
    private final RedisUtil redisUtil;
    private final RedisTemplate<String,String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final String BEARER_TYPE = "Bearer";

    public CommonResponseDto signup(SignupInfo signupInfo) throws Exception {
        String encodePwd =  passwordEncoder.encode(signupInfo.getPassword());
        // 이미 이메일로 회원가입되어있는지 확인
        if(!checkDuplicatedEmail(signupInfo.getEmail())) throw new RuntimeException("이미 가입되어있는 이메일 입니다.");
        // 이름, 전화번호, 주소, 이메일 암호화
        String encryptRegistNum = aesUtil.encrypt(signupInfo.getRegistration());
        String encryptName = aesUtil.encrypt(signupInfo.getName());
        String encryptPhoneNum = aesUtil.encrypt(signupInfo.getPhonenum());
        String encryptEmail = aesUtil.encrypt(signupInfo.getEmail());
        String encryptAddress = aesUtil.encrypt(signupInfo.getAddress());
        Users user = Users.builder().email(encryptEmail).password(encodePwd)
                .name(encryptName).address(encryptAddress)
                .phoneNum(encryptPhoneNum).registrationNum(encryptRegistNum).build();
        usersRepository.save(user);
        sendCodeToEmail(signupInfo.getEmail(),user);
        return CommonResponseDto.builder().statusCode(200).success(true).message("회원가입이 완료되었습니다! 이메일 인증 메일을 보냈습니다. 인증 후 서비스 이용이 가능합니다").build();
    }


    private boolean checkDuplicatedEmail(String email){
        Optional<Users> user = usersRepository.findByEmail(aesUtil.encrypt(email));
        if(user.isPresent()){
            return false;
        }
        return true;
    }


    public void sendCodeToEmail(String toEmail, Users user) throws MessagingException {

        String title = "Whatever with me 이메일 인증 확인";
        String authCode = UUID.randomUUID().toString();
        if(mailService.sendEmail(toEmail,title,authCode)){
            redisUtil.set(authCode,user,10);
        };
    }


    public void registerUser(String key) throws TimeoutException {

        Users user = (Users) redisUtil.get(key);
        if(user==null) throw new TimeoutException(); // 예외 처리예정
        Users updateUser = usersRepository.findByEmail(user.getEmail()).orElseThrow();
        updateUser.setRole(Role.USER); // 회원으로 등록
        usersRepository.save(updateUser);
    }
// todo : 만약 인증 시간 만료로 인증 못했을 시 다시 인증번호 전송하도록

    public ResponseEntity login(LoginInfo loginInfo, HttpServletResponse httpServletResponse) {
        Users user = usersRepository.findByEmail(aesUtil.encrypt(loginInfo.getEmail())).orElseThrow();// "이메일과 일치하는 유저가 존재하지않습니다. 확인해주세요"

        if(user.getRole()==null){
            // todo 만약 인증확인 메일이 유효시간이 있다면, 다시 메일을 보내주는 작업을 추가
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 인증이 완료되지 않은 회원입니다. 이메일 인증을 완료해주세요");
        }

        Map<String,String> response = new HashMap<>();

        try{
            UsernamePasswordAuthenticationToken authenticationToken = toAuthentication(user.getEmail(),user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            String accessToken = "";
            String refreshToken = "";

            if(!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) throw new RuntimeException("비밀번호가 틀렸습니다. 다시 입력해주세요");

            accessToken = jwtTokenProvider.createAccessToken(user);
            refreshToken = jwtTokenProvider.createRefreshToken(user);

            redisTemplate.opsForValue().set("RF: "+user.getEmail(),refreshToken,Duration.ofHours(3L));

            httpServletResponse.addCookie(new Cookie("refresh_token",refreshToken));
            response.put("http_status", HttpStatus.OK.toString());
            response.put("message","로그인되었습니다");
            response.put("token_type",BEARER_TYPE);
            response.put("access_token",accessToken);
            response.put("roles",user.getRole().toString());
            return ResponseEntity.ok(response);
        } catch(BadCredentialsException be){
            throw new RuntimeException();
        }

    }

    private UsernamePasswordAuthenticationToken toAuthentication(String email,String pwd) {
        return new UsernamePasswordAuthenticationToken(email,pwd);
    }

    public ResponseEntity logout(String accessToken) {
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        String email = jwtTokenProvider.getEmailByToken(accessToken);
        redisTemplate.opsForValue().set(accessToken,"logout",expiration, TimeUnit.MILLISECONDS);
        redisUtil.delete("RF: "+email);
        return ResponseEntity.status(HttpStatus.OK).body("정상적으로 로그아웃되었습니다.");
    }

    public ResponseEntity refresh(String token) {
        String email = jwtTokenProvider.getEmailByToken(token); // 암호화된 이메일
        String accessToken = checkByToken(email);
        if(redisTemplate.opsForValue().get("RF: "+email)!=null){
            Map<String,String> response = new HashMap<>();
            response.put("access_token",accessToken);
            response.put("http_status",HttpStatus.CREATED.toString());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 유저입니다");
    }

    private String checkByToken(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow();
        return jwtTokenProvider.createAccessToken(user);
    }
}


