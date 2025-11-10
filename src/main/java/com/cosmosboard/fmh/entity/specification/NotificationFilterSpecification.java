package com.cosmosboard.fmh.entity.specification;

import com.cosmosboard.fmh.entity.Notification;
import com.cosmosboard.fmh.entity.specification.criteria.NotificationCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("NullableProblems")
public class NotificationFilterSpecification implements Specification<Notification> {
    private static final String USER = "user";

    private static final String ID = "id";

    private static final String MESSAGE = "message";

    private static final String FORMAT = "%%%s%%";

    private final NotificationCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getUserId() != null) {
            predicates.add(builder.equal(root.get(USER).get(ID), criteria.getUserId()));
        }

        if (criteria.getQ() != null) {
            predicates.add(builder.like(
                    builder.lower(root.get(MESSAGE)),
                    String.format(FORMAT, criteria.getQ().toLowerCase())
            ));
        }

        return query.where(predicates.toArray(new Predicate[0])).distinct(true).getRestriction();
    }
}