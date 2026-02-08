package com.pcbuilder.core.modules.user.sevice;

import com.pcbuilder.core.modules.user.dto.UserSummaryDto;
import com.pcbuilder.core.modules.user.model.UserEntity;
import com.pcbuilder.core.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProvider {

    private final UserRepository userRepository;

    public Optional<UserSummaryDto> getUserSummary(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserSummaryDto(
                        user.getId(),
                        user.getUsername(),
                        user.getAvatarFileName()
                ));
    }

    public Optional<Long> getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserEntity::getId);
    }

    public Map<Long, UserSummaryDto> getUsersSummaryByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        user -> new UserSummaryDto(user.getId(), user.getUsername(), user.getAvatarFileName())
                ));
    }
}
