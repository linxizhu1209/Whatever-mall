package org.book.commerce.userservice.domain;

//@Primary
//@RequiredArgsConstructor
//@Setter
//@Service
//public class CustomUserDetailService implements UserDetailsService {
//
//    private final UsersRepository usersRepository;
//
//
//    @Override
//    public UserDetails loadUserByUsername(String userEmail) {
//        Users user = usersRepository.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("일치하는 회원을 찾을 수 없습니다."));
//        return new CustomUserDetails(user);
//    }
//}
