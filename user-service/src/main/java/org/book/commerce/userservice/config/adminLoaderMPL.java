package org.book.commerce.userservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.book.commerce.common.security.Users;
import org.book.commerce.common.security.UsersRepository;
import org.book.commerce.common.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class adminLoaderMPL implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final AESUtil aesUtil;
    private final UsersRepository usersRepository;
    @Override
    public void run(String... args) throws Exception {
        createDefaultUser();
    }

    @Value("${spring.default.admin_id}")
    private String defaultUserId;
    @Value("${spring.default.admin_pwd}")
    private String defaultPassword;

    private void createDefaultUser(){
        String defaultName = "관리자";
        String defaultAddress = "경기도 수원시";
        String defaultPhoneNum = "010-1234-5678";

        if(!usersRepository.existsByEmail(aesUtil.encrypt(defaultUserId))){
            Users defaultUser = Users.builder()
                    .email(aesUtil.encrypt(defaultUserId)).address(aesUtil.encrypt(defaultAddress))
                    .phoneNum(aesUtil.encrypt(defaultPhoneNum)).name(defaultName)
                    .password(passwordEncoder.encode(defaultPassword))
                    .role(Users.Role.ADMIN).build();
            log.info("관리자 계정이 생성되었습니다");
            usersRepository.save(defaultUser);
        } else {
            log.info("이미 관리자 계정이 존재하여 생성하지 않습니다.");
        }
    }
}
