package com.example.BackendServer.repository;

import com.example.BackendServer.entity.History;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.BackendServer.entity.History.Status.NOT_RETURNED;
import static com.example.BackendServer.entity.History.Status.RETURNED;
import static com.example.BackendServer.entity.QHistory.history;
import static com.example.BackendServer.entity.QPay.pay;
import static com.example.BackendServer.entity.mat.QMat.mat;
import static com.example.BackendServer.entity.user.QUser.user;

@RequiredArgsConstructor
@Slf4j
public class HistoryCustomRepositoryImpl implements HistoryCustomRepository{

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<History> searchHistoryBy(History.Status status, Pageable pageable) {

        List<History> content = queryFactory
                .selectFrom(history)
                .join(history.user, user).fetchJoin()
                .join(history.pay, pay).fetchJoin()
                .join(history.mat, mat).fetchJoin()
                .where(statusCheck(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(history.count())
                .from(history)
                .where(statusCheck(status));


        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression statusCheck(History.Status statusCond) {
        return  statusCond == null ? null : history.status.eq(statusCond);
    }
}
