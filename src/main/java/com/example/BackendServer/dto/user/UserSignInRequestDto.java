package com.example.BackendServer.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignInRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Schema(example = "승진")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+|<>?:{}])[A-Za-z\\d~!@#$%^&*()_+|<>?:{}]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    @Schema(example = "qwer1234@#")
    private String password;
}
