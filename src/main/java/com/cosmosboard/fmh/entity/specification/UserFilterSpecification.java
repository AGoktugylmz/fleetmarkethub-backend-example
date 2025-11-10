package com.cosmosboard.fmh.entity.specification;

import com.cosmosboard.fmh.entity.Company;
import com.cosmosboard.fmh.entity.Role;
import com.cosmosboard.fmh.entity.User;
import com.cosmosboard.fmh.entity.specification.criteria.UserCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("NullableProblems")
public class UserFilterSpecification implements Specification<User> {

    private static final String AVATAR = "avatar";

    private static final String BLOCKED_AT = "blockedAt";

    private static final String COMPANY = "company";

    private static final String EMPLOYEES = "employees";

    private static final String CREATED_AT = "createdAt";

    private static final String DESCRIPTION = "description";

    private static final String EMAIL = "email";

    private static final String EMAIL_ACTIVATED_AT = "emailActivatedAt";

    private static final String GSM = "gsm";

    private static final String LAST_NAME = "lastName";

    private static final String NAME = "name";

    private static final String ROLES = "roles";

    private static final String TITLE = "title";

    private static final String FORMAT = "%%%s%%";

    private final UserCriteria criteria;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getRoles() != null && !criteria.getRoles().isEmpty()) {
            Join<User, Role> roleJoin = root.join(ROLES);

            predicates.add(
                builder.in(roleJoin.get(NAME)).value(criteria.getRoles())
            );
        }

        if (criteria.getIsAvatar() != null) {
            if (criteria.getIsAvatar()) {
                predicates.add(builder.isNotNull(root.get(AVATAR)));
            } else {
                predicates.add(builder.isNull(root.get(AVATAR)));
            }
        }

        if (criteria.getCreatedAtStart() != null) {
            predicates.add(
                builder.greaterThanOrEqualTo(root.get(CREATED_AT), criteria.getCreatedAtStart())
            );
        }

        if (criteria.getCreatedAtEnd() != null) {
            predicates.add(
                builder.lessThanOrEqualTo(root.get(CREATED_AT), criteria.getCreatedAtEnd())
            );
        }

        if (criteria.getIsEmailActivated() != null) {
            if (criteria.getIsEmailActivated()) {
                predicates.add(builder.isNotNull(root.get(EMAIL_ACTIVATED_AT)));
            } else {
                predicates.add(builder.isNull(root.get(EMAIL_ACTIVATED_AT)));
            }
        }

        if (criteria.getIsBlocked() != null) {
            if (criteria.getIsBlocked()) {
                predicates.add(builder.isNotNull(root.get(BLOCKED_AT)));
            } else {
                predicates.add(builder.isNull(root.get(BLOCKED_AT)));
            }
        }

        if (criteria.getQ() != null) {

            Join<User, Company> companies = root.join(EMPLOYEES);
            Path<String> company = companies.get(COMPANY);

            predicates.add(
                builder.and(
                    builder.or(
                        builder.like(builder.lower(root.get(EMAIL)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(root.get(TITLE)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(root.get(NAME)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(root.get(LAST_NAME)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(root.get(GSM)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(company.get(NAME)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(company.get(DESCRIPTION)), String.format(FORMAT,
                            criteria.getQ().toLowerCase()))
                    )
                )
            );
        }
        return query.where(predicates.toArray(new Predicate[0])).distinct(true).getRestriction();
    }
}
