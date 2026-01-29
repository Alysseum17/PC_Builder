package com.pcbuilder.core.modules.auth.userdetails;

import com.pcbuilder.core.modules.user.repository.UserRepository;
import com.pcbuilder.core.modules.user.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        UserEntity userEntity = userRepository.findByUsernameOrEmail(login, login).orElseThrow(
                () -> new UsernameNotFoundException("User not found with login: " + login)
        );
        return UserPrincipal.create(userEntity);
    }

    public UserPrincipal loadUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id: " + id)
        );
        return UserPrincipal.create(userEntity);
    }
}
