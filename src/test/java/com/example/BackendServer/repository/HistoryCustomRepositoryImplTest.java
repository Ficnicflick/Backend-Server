package com.example.BackendServer.repository;

import com.example.BackendServer.common.config.QueryDslConfig;
import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.Pay;
import com.example.BackendServer.entity.mat.Mat;
import com.example.BackendServer.entity.mat.MatCheck;
import com.example.BackendServer.entity.mat.Place;
import com.example.BackendServer.entity.user.Provider;
import com.example.BackendServer.entity.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
import java.util.List;

import static com.example.BackendServer.entity.History.Status.NOT_RETURNED;
import static com.example.BackendServer.entity.History.Status.RETURNED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class HistoryCustomRepositoryImplTest {


    @Autowired HistoryRepository historyRepository;
    @Autowired EntityManager em;

    @BeforeEach
    public void before(){
        Mat mat1 = createMat(); Mat mat2 = createMat(); Mat mat3 = createMat();
        //em.persist(mat1); em.persist(mat2); em.persist(mat3);

        User user1 = createuser("aaaa");
        User user2 = createuser("bbbb");
        //em.persist(user1); em.persist(user2);

        Pay pay1 = createPay("1"); Pay pay2 = createPay("2"); Pay pay3 = createPay("3");
        Pay pay4 = createPay("4"); Pay pay5 = createPay("5"); Pay pay6 = createPay("6");
        //em.persist(pay1);em.persist(pay2);em.persist(pay3);
        //em.persist(pay4);em.persist(pay5);em.persist(pay6);

        History history1 = createHistory(user1, pay1, RETURNED);
        History history2 = createHistory(user1, pay2, RETURNED);
        History history3 = createHistory(user1, pay3, NOT_RETURNED);
        History history4 = createHistory(user2, pay4, RETURNED);
        History history5 = createHistory(user2, pay5, NOT_RETURNED);
        History history6 = createHistory(user2, pay6, RETURNED);

        //em.persist(history1); em.persist(history2); em.persist(history3);
        //em.persist(history4); em.persist(history5); em.persist(history6);

    }

    @Test
    @DisplayName("조건에 맞는 이용내역을 페이징 조회한다.")
    @Transactional
    void searchHistoryBy(){
        //given


        PageRequest pageRequest = PageRequest.of(0, 5);
        //when
        Page<History> histories = historyRepository.searchHistoryBy(RETURNED, pageRequest);
        System.out.println("histories.getSize() = " + histories.getSize());
        for(History history : histories){
            System.out.println(history.getStatus());
        }

        //then
        assertThat(histories.getSize()).isEqualTo(4);

    }

    private static History createHistory(User user, Pay pay, History.Status status) {
        return History.builder()
                .pay(pay)
                .user(user)
                .status(status)
                .cnt(1)
                .build();
    }

    private static Pay createPay(String tid) {
        return Pay.builder()
                .tid(tid)
                .item_name("pic")
                .total(7000)
                .quantity(1)
                .build();
    }

    private static User createuser(String email) {
        return User.builder()
                .provider(Provider.KAKAO)
                .email(email)
                .nickname("gogo")
                .nicknameUpdateAt(LocalDate.MIN)
                .roles(List.of("ROLE_ADMIN"))
                .build();
    }

    private static Mat createMat() {
        return Mat.builder()
                .price(5000)
                .place(Place.TTUKSEOM_HAN_RIVER1)
                .matCheck(MatCheck.builder().build())
                .build();
    }

}