//package com.piotrek.diet.security;
//
//import com.piotrek.diet.helpers.exceptions.NotFoundException;
//import com.piotrek.diet.user.User;
//import com.piotrek.diet.user.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import static java.util.Collections.emptyList;
//
//@Service
//@RequiredArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserService userService;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        // right now i need to find user by email :V
//        User user = userService.findByEmail(email).block();
//        if (user == null)
//            throw new NotFoundException("Not found user with email: " + email);
//        System.out.println("Znalazłem usera z mailem: " + email);
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), null, emptyList());
//    }
//}
