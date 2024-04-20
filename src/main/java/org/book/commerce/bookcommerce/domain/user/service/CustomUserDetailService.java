package org.book.commerce.bookcommerce.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.book.commerce.bookcommerce.domain.user.repository.UsersRepository;
import org.book.commerce.bookcommerce.domain.user.domain.CustomUserDetails;
import org.book.commerce.bookcommerce.domain.user.domain.Users;
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
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(userEmail).orElseThrow();
        return new CustomUserDetails(user);
    }
}
