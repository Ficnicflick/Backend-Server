package com.example.BackendServer.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Collections;

@Getter @Setter
public class UserSignUpRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(example = "승진")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+|<>?:{}])[A-Za-z\\d~!@#$%^&*()_+|<>?:{}]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    @Schema(example = "qwer1234@#")
    private String password;

    @NotBlank(message = "재확인 비밀번호를 입력해주세요.")
    @Schema(example = "qwer1234@#")
    private String checkedPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, message = "닉네임은 최소 2자 이상으로 입력해주세요.")
    @Schema(example = "헬로키티")
    private String nickname;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    @Schema(example = "qwer1234@naver.com")
    private String email;


    @Builder
    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .email(email)
                .roles(Collections.singletonList("USER"))
                .build();
    }
}
