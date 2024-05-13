package com.example.BackendServer.repository;

import com.example.BackendServer.entity.History;
import com.example.BackendServer.entity.QHistory;
import com.example.BackendServer.entity.user.QUser;
import com.example.BackendServer.entity.user.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.BackendServer.entity.History.Status.NOT_RETURNED;
import static com.example.BackendServer.entity.QHistory.history;
import static com.example.BackendServer.entity.user.QUser.user;


@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{
    private final JPAQueryFactory queryFactory;
    private final static int RENTAL_TIME = 6;

    @Override
    public Optional<User> searchUserWithUsedHistories(String socialId) {
        User findUser = queryFactory
                .select(user)
                .from(user)
                .join(history).on(eqUsedState().and(checkTime(history)))
                .where(eqUser(socialId))
                .fetchOne();
        return Optional.ofNullable(findUser);
    }

    private BooleanExpression checkTime(QHistory history) {
        return history.started_time.after(LocalDateTime.now().minusHours(RENTAL_TIME));
    }

    private BooleanExpression eqUsedState() {
        return history.status.eq(NOT_RETURNED);
    }

    private BooleanExpression eqUser(String socialId) {
        return user.socialId.eq(socialId);
    }
}
