package kahlua.KahluaProject.service;

import kahlua.KahluaProject.global.apipayload.code.status.ErrorStatus;
import kahlua.KahluaProject.converter.UserConverter;
import kahlua.KahluaProject.domain.user.*;
import kahlua.KahluaProject.dto.user.request.SignUpRequest;
import kahlua.KahluaProject.dto.user.request.UserInfoRequest;
import kahlua.KahluaProject.dto.user.response.UserResponse;
import kahlua.KahluaProject.global.exception.GeneralException;
import kahlua.KahluaProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User createUser(Credential credential, SignUpRequest signUpRequest) {
        userRepository.findByEmailAndDeletedAtIsNull(signUpRequest.getEmail()).ifPresent(existingUser -> {
            throw new GeneralException(ErrorStatus.ALREADY_EXIST_USER);
        });
        User user = signUpRequest.toUser(credential);
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    @Transactional
    public void withdraw(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse createUserInfo(Long userId, UserInfoRequest userInfoRequest) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.updateUserInfo(userInfoRequest);

        return UserConverter.toUserResDto(user);
    }
}
