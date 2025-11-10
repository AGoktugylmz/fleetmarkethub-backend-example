package com.cosmosboard.fmh.entity.specification;

import com.cosmosboard.fmh.entity.Offer;
import com.cosmosboard.fmh.entity.specification.criteria.OfferCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("NullableProblems")
public class OfferFilterSpecification implements Specification<Offer> {

    private static final String FORMAT = "%%%s%%";

    private static final String COMPANY = "company";

    private static final String CAR = "car";

    private static final String NAME = "name";

    private static final String CONTENT = "content";

    private static final String DESCRIPTION = "description";

    private static final String TITLE = "title";

    private static final String STATUS = "status";

    private static final String PRICE = "price";

    private static final String ID = "id";

    private static final String STATUS_MESSAGE = "statusMessage";

    private final OfferCriteria criteria;

    @Override
    public Predicate toPredicate(Root<Offer> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria == null) {
            return null;
        }

        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getCarId() != null) {
            predicates.add(
                builder.in(root.get(CAR).get(ID)).value(criteria.getCarId())
            );
        }

        if (criteria.getCompanyId() != null) {
            predicates.add(
                builder.in(root.get(COMPANY).get(ID)).value(criteria.getCompanyId())
            );
        }

        if (criteria.getCarOwnerCompanyId() != null) {
            predicates.add(
                builder.in(root.get("carOwnerCompany").get(ID)).value(criteria.getCarOwnerCompanyId())
            );
        }

        if (criteria.getPrice() != null) {
            predicates.add(
                builder.equal(root.get(PRICE), criteria.getPrice())
            );
        }

        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            predicates.add(
                builder.in(root.get(STATUS)).value(criteria.getStatuses())
            );
        }

        if (criteria.getQ() != null) {
            Path<String> car = root.get(CAR);
            Path<String> company = root.get(COMPANY);

            predicates.add(
                builder.and(
                    builder.or(
                        builder.like(builder.lower(car.get(TITLE)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(car.get(CONTENT)), String.format(FORMAT,
                            criteria.getQ().toLowerCase())),
                        builder.like(builder.lower(car.get(STATUS_MESSAGE)), String.format(FORMAT,
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
