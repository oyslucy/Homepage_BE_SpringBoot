package kahlua.KahluaProject.converter;

import kahlua.KahluaProject.domain.user.User;
import kahlua.KahluaProject.dto.response.UserResponse;

public class UserConverter {

    public static UserResponse toUserResDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getUserType())
                .build();
    }
}
