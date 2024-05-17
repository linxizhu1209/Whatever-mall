package org.book.commerce.userservice.service;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.dto.CommonResponseDto;
import org.book.commerce.common.entity.ErrorCode;
import org.book.commerce.common.exception.CommonException;
import org.book.commerce.common.exception.ConflictException;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.common.util.AESUtil;
import org.book.commerce.common.util.RedisUtil;
import org.book.commerce.userservice.config.JwtTokenProvider;
import org.book.commerce.userservice.domain.Users;
import org.book.commerce.userservice.dto.EmailInfo;
import org.book.commerce.userservice.dto.LoginInfo;
import org.book.commerce.userservice.dto.SignupInfo;
import org.book.commerce.userservice.repository.UsersRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public CommonResponseDto signup(SignupInfo signupInfo){
        String encodePwd =  passwordEncoder.encode(signupInfo.getPassword());
        // 이미 이메일로 회원가입되어있는지 확인
        if(!checkDuplicatedEmail(signupInfo.getEmail())) throw new ConflictException("이미 가입되어있는 이메일 입니다.");
        // 이름, 전화번호, 주소, 이메일 암호화
        String encryptRegistNum = aesUtil.encrypt(signupInfo.getRegistration());
        String encryptName = aesUtil.encrypt(signupInfo.getName());
        String encryptPhoneNum = aesUtil.encrypt(signupInfo.getPhoneNum());
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


    public void sendCodeToEmail(String toEmail, Users user){

        String title = "Whatever with me 이메일 인증 확인";
        String authCode = UUID.randomUUID().toString();
        if(mailService.sendEmail(toEmail,title,authCode)){
            redisUtil.set(authCode,user,10);
        };
    }


    public CommonResponseDto registerUser(String key) {
        Users user = (Users) redisUtil.get(key);
        if(user==null) throw new CommonException("인증시간이 만료되었습니다. 가입하신 이메일 입력 후 인증메일 재전송 버튼을 눌러주세요.", ErrorCode.UNAUTHORIZED_RESPONSE);
        Users updateUser = findUserByEmail(user.getEmail());
        updateUser.setRole(Users.Role.USER); // 회원으로 등록
        usersRepository.save(updateUser);
        return CommonResponseDto.builder().statusCode(200).success(true).message("회원인증이 성공하였습니다").build();
    }

    public CommonResponseDto resendEmail(EmailInfo emailInfo) {
        String email = aesUtil.encrypt(emailInfo.getEmail());
        Users user = findUserByEmail(email);
        sendCodeToEmail(emailInfo.getEmail(),user);
        return CommonResponseDto.builder().statusCode(200).success(true).message("이메일 인증 메일이 재전송되었습니다. 10분이내에 인증을 완료해주세요!").build();
    }

    public ResponseEntity login(LoginInfo loginInfo, HttpServletResponse httpServletResponse) {
        String email = aesUtil.encrypt(loginInfo.getEmail());
        Users user = findUserByEmail(email);
        if(user.getRole()==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 인증이 완료되지 않은 회원입니다. 이메일 인증을 완료해주세요");
        }

        Map<String,String> response = new HashMap<>();
        UsernamePasswordAuthenticationToken authenticationToken = toAuthentication(user.getEmail(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String accessToken = "";
        String refreshToken = "";

        if(!passwordEncoder.matches(loginInfo.getPassword(), user.getPassword())) throw new ConflictException("비밀번호가 틀렸습니다. 다시 입력해주세요");

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
    }

    private UsernamePasswordAuthenticationToken toAuthentication(String email,String pwd) {
        return new UsernamePasswordAuthenticationToken(email,pwd);
    }

    public CommonResponseDto logout(String accessToken) {
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        String email = jwtTokenProvider.getEmailByToken(accessToken);
        redisTemplate.opsForValue().set(accessToken,"logout",expiration, TimeUnit.MILLISECONDS);
        redisUtil.delete("RF: "+email);
        return CommonResponseDto.builder().statusCode(200).success(true).message("정상적으로 로그아웃되었습니다.").build();
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
        Users user = findUserByEmail(email);
        return jwtTokenProvider.createAccessToken(user);
    }

    public Users findUserByEmail(String email){
        return usersRepository.findByEmail(email).orElseThrow(()->new NotFoundException("입력하신 이메일과 일치하는 회원이 존재하지 않습니다. 확인해주세요. 입력 이메일: "+email));
    }
}


