package org.book.commerce.userservice.service;

import lombok.RequiredArgsConstructor;
import org.book.commerce.common.dto.CommonResponseDto;
import org.book.commerce.common.exception.NotFoundException;
import org.book.commerce.common.util.AESUtil;
import org.book.commerce.userservice.domain.Users;
import org.book.commerce.userservice.dto.MyPageDto;
import org.book.commerce.userservice.dto.UpdateInfo;
import org.book.commerce.userservice.repository.UsersRepository;
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
    public MyPageDto getMypage(String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(()->new NotFoundException("회원을 찾을 수 없습니다"));
        return MyPageDto.builder().email(aesUtil.decrypt(user.getEmail())).address(aesUtil.decrypt(user.getAddress()))
                .phonenum(aesUtil.decrypt(user.getPhoneNum())).build();
    }

    public CommonResponseDto updateMyPage(String email, UpdateInfo updateInfo) {
        Users user = usersRepository.findByEmail(email).orElseThrow(()->new NotFoundException("회원을 찾을 수 없습니다."));
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
        return CommonResponseDto.builder().success(true).statusCode(200).message("회원정보 수정이 완료되었습니다").build();
    }
}
