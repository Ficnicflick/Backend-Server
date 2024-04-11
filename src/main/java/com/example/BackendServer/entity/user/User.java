package com.example.BackendServer.entity.user;

import com.example.BackendServer.dto.KakaoProfile;
import com.example.BackendServer.entity.History;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity @Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(updatable = false, unique = true)
    private String socialId; // 소셜 로그인의 경우에 전화번호, 이메일 등을 키 값으로 저장할 수 없음

    @Column(updatable = false, unique = true)
    private String temporaryPassword;

    @Column(nullable = false) // 닉네임도 소셜마다 변경 및 중복 가능성 존재
    private String nickname;

    @Column(nullable = false)
    private int warningCnt;

    @Column(nullable = false)
    private double echoRate;

    /*@Enumerated(EnumType.STRING)
    private Role role;*/

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Column // 이메일도 소셜마다 변경 및 중복 가능성 존재
    private String email;

    // 이용내역 list
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> histories = new ArrayList<>();


    /*
    @Column // 핸드폰 번호 변경 가능. 여러 소셜 로그인으로 회원가입 한 경우에도 번호가 같을 수 있음.
    private String phoneNumber;
    // 일단 email과 phoneNumber 제외 -> 서비스를 사용한다면 email과 phonenumbe 필요 -> 사용 문제에 대한 연락 필요하기 때문에*/

    @Builder
    public User(KakaoProfile profile, Provider provider) {
        this.socialId = profile.getId();
        this.temporaryPassword = profile.id + "_" + provider.getText();
        this.nickname = profile.getProperties().nickname;
        this.email = profile.getKakao_account().email;
        this.roles = Collections.singletonList("ROLE_USER");
        this.warningCnt = 0;
        this.echoRate = 0.0;
        this.provider = provider;
    }

    public static User toEntity(KakaoProfile profile, Provider provider){
        return User.builder()
                .socialId(profile.getId())
                .temporaryPassword(profile.id + "_" + provider.getText())
                .roles(Collections.singletonList("ROLE_USER"))
                .provider(provider)
                .nickname(profile.getProperties().nickname)
                .email(profile.getKakao_account().email)
                .warningCnt(0)
                .echoRate(0.0)
                .build();
    }

    public void addHistory(History history) {
        this.histories.add(history);
        history.setUser(this);
    }
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.temporaryPassword = passwordEncoder.encode(temporaryPassword);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
