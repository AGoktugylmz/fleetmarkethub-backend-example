package com.cosmosboard.fmh.util;

import com.cosmosboard.fmh.entity.specification.criteria.PaginationCriteria;
import com.cosmosboard.fmh.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public final class PageRequestBuilder {
    private PageRequestBuilder() {}

    public static PageRequest build(PaginationCriteria paginationCriteria) {
        if (paginationCriteria.getPage() == null || paginationCriteria.getPage() < 1) {
            log.warn("Page number is not valid");
            throw new BadRequestException("Page must be greater than 0!");
        }

        paginationCriteria.setPage(paginationCriteria.getPage() - 1);

        if (paginationCriteria.getSize() == null || paginationCriteria.getSize() < 1) {
            log.warn("Page size is not valid");
            throw new BadRequestException("Size must be greater than 0!");
        }

        PageRequest pageRequest = PageRequest.of(paginationCriteria.getPage(), paginationCriteria.getSize());

        if (paginationCriteria.getSortBy() != null && paginationCriteria.getSort() != null) {
            Sort.Direction direction = Sort.Direction.ASC;

            if (paginationCriteria.getSort().equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }

            List<String> columnsList = new ArrayList<>(Arrays.asList(paginationCriteria.getColumns()));
            columnsList.add(paginationCriteria.getSortBy());

            if (columnsList.contains(paginationCriteria.getSortBy())) {
                return pageRequest.withSort(Sort.by(direction, paginationCriteria.getSortBy()));
            }
        }

        return pageRequest;
    }
}
