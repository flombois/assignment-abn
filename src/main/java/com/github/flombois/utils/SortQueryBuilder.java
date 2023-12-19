package com.github.flombois.utils;

import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

import java.util.List;
import java.util.Objects;


import static io.quarkus.panache.common.Sort.Direction.Ascending;
import static io.quarkus.panache.common.Sort.Direction.Descending;

/**
 * This class is responsible for converting a list of query parameters into a
 * {@link io.quarkus.panache.common.Sort Sort} instance, which can be used in database queries
 * to specify the sorting order of the results.
 */
public final class SortQueryBuilder {

    private SortQueryBuilder() {}

    /**
     * Parses a list of query parameters and builds a {@link io.quarkus.panache.common.Sort Sort} instance.
     *
     * @param queryParams The list of query parameters representing sorting criteria.
     * @return A {@link io.quarkus.panache.common.Sort Sort} instance based on the provided query parameters.
     * @throws NullPointerException If the {@code queryParams} parameter is {@code null}.
     */
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
