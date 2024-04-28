package org.book.commerce.common.security;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Primary
@RequiredArgsConstructor
@Setter
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String userEmail) {
        Users user = usersRepository.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("일치하는 회원을 찾을 수 없습니다."));
        return new CustomUserDetails(user);
    }
}
