package org.book.commerce.bookcommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.book.commerce.bookcommerce.repository.UsersRepository;
import org.book.commerce.bookcommerce.repository.entity.CustomUserDetails;
import org.book.commerce.bookcommerce.repository.entity.Users;
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
