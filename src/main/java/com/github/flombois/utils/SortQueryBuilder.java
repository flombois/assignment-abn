package com.github.flombois.utils;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

import java.util.List;
import java.util.Objects;


import static io.quarkus.panache.common.Sort.Direction.Ascending;
import static io.quarkus.panache.common.Sort.Direction.Descending;

public final class SortQueryBuilder {

    private SortQueryBuilder() {}

    public static Sort parse(List<String> queryParams) {
        Objects.requireNonNull(queryParams);
        Sort sortQuery = Sort.empty();
        for(String queryParam: queryParams) {
            Direction direction = Ascending;
            if(queryParam.startsWith("-") ) {
                direction = Descending;
                // Shift string left by one char to remove '-' prefix
                queryParam = queryParam.substring(1);
            }
            sortQuery.and(queryParam, direction);
        }
        return sortQuery;

    }
}
