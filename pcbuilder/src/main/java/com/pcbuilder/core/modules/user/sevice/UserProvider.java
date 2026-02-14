package com.pcbuilder.core.modules.user.sevice;

import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProvider {

    private final UserRepository userRepository;

    public Optional<Long> getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserEntity::getId);
    }
}
