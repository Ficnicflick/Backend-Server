package com.example.BackendServer.entity.user;

import com.example.BackendServer.dto.KakaoProfile;
import com.example.BackendServer.entity.History;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

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

    @Column(nullable = false)
    private LocalDate nicknameUpdateAt;

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
        //this.roles = Collections.singletonList("ROLE_USER");
        this.warningCnt = 0;
        this.echoRate = 0.0;
        this.nicknameUpdateAt = LocalDate.now();
        this.provider = provider;
    }

    public static User toEntity(KakaoProfile profile, Provider provider){
        User user = User.builder()
                .socialId(profile.getId())
                .temporaryPassword(profile.id + "_" + provider.getText())
                .provider(provider)
                .nickname(profile.getProperties().nickname)
                .email(profile.getKakao_account().email)
                .warningCnt(0)
                .echoRate(0.0)
                .nicknameUpdateAt(LocalDate.now())
                .build();
        String role = "3381414174".equals(profile.id) ? (role = "ROLE_ADMIN") : (role = "ROLE_USER");
        user.getRoles().add(role);
        return user;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return warningCnt == user.warningCnt && Double.compare(user.echoRate, echoRate) == 0 && Objects.equals(id, user.id) &&
                Objects.equals(socialId, user.socialId) && Objects.equals(temporaryPassword, user.temporaryPassword) &&
                Objects.equals(nickname, user.nickname) && provider == user.provider && Objects.equals(roles, user.roles) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, socialId, temporaryPassword, nickname, warningCnt, echoRate, provider, roles, email, histories);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setNicknameUpdateAt(LocalDate nicknameUpdateAt) {
        this.nicknameUpdateAt = nicknameUpdateAt;
    }

    public void plusWarningCnt() {
        this.warningCnt++;
    }

    public void setEchoRate(double echoRate) {
        this.echoRate = echoRate;
    }
}
