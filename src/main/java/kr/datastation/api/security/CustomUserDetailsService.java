package kr.datastation.api.security;

import kr.datastation.api.advice.ResourceNotFoundException;
import kr.datastation.api.model.user.User;
import kr.datastation.api.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CustomUserDetailsService implements UserDetailsService {

    final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
        );
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        // This is compatible with Spring Boot 2.2.x
//        User user = userRepository.findById(id).orElseThrow(
//            () -> new ResourceNotFoundException("User", "id", id)
//        );
        User user = userRepository.findById(id);
        if (user == null)
            throw new ResourceNotFoundException("User", "id", id);
        return UserPrincipal.create(user);
    }
}