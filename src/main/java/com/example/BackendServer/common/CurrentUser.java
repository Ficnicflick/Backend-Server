package com.example.BackendServer.common;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)// 타겟은 파라미터에만 붙이겠다.
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : username")
public @interface CurrentUser {
}
