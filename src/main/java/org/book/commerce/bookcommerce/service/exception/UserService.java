package org.book.commerce.bookcommerce.service.exception;

import lombok.RequiredArgsConstructor;
import org.book.commerce.bookcommerce.config.AESUtil;
import org.book.commerce.bookcommerce.controller.dto.MyPageDto;
import org.book.commerce.bookcommerce.controller.dto.UpdateInfo;
import org.book.commerce.bookcommerce.repository.UsersRepository;
import org.book.commerce.bookcommerce.repository.entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final AESUtil aesUtil;
    private final PasswordEncoder passwordEncoder;
    public ResponseEntity<MyPageDto> getMypage(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow();
        MyPageDto mypage = MyPageDto.builder().email(aesUtil.decrypt(user.getEmail())).address(aesUtil.decrypt(user.getAddress()))
                .phonenum(aesUtil.decrypt(user.getPhoneNum())).build();
        return ResponseEntity.status(HttpStatus.OK).body(mypage);
    }

    public ResponseEntity updateMyPage(String email, UpdateInfo updateInfo) {
        Users user = usersRepository.findByEmail(email).orElseThrow();
        String address = updateInfo.getAddress();
        String password = updateInfo.getPassword();
        String phoneNum = updateInfo.getPhoneNum();
        if(address!=null){
            user.setAddress(aesUtil.encrypt(address));
        }
        if(password!=null){
            user.setPassword(passwordEncoder.encode(password));
        }
        if(phoneNum!=null){
            user.setPhoneNum(aesUtil.encrypt(phoneNum));
        }
        usersRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("회원정보 수정이 완료되었습니다");
    }
}
