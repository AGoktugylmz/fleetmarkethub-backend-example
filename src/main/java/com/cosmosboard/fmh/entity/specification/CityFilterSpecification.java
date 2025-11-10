package com.cosmosboard.fmh.entity.specification;

import com.cosmosboard.fmh.entity.City;
import com.cosmosboard.fmh.entity.specification.criteria.CityCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("NullableProblems")
public class CityFilterSpecification implements Specification<City> {

    private static final String FORMAT = "%%%s%%";

    private static final String CODE = "code";

    private static final String NAME = "name";


    private final CityCriteria criteria;

    @Override
    public Predicate toPredicate(Root<City> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getQ() != null) {
            predicates.add(
                builder.and(
                    builder.or(
                        builder.like(builder.lower(root.get(NAME)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(root.get(CODE)), String.format(FORMAT,
                            criteria.getQ().toLowerCase()))
                    )
                )
            );
        }

        return query.where(predicates.toArray(new Predicate[0])).distinct(true).getRestriction();
    }
}
