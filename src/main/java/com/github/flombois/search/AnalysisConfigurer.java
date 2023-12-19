package com.github.flombois.search;

import io.quarkus.hibernate.search.orm.elasticsearch.SearchExtension;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

/**
 *
 * This class implements the {@link ElasticsearchAnalysisConfigurer} interface to provide
 * configuration options for Elasticsearch analysis during Hibernate Search initialization.
 * Usage:
 * - Configures a custom analyzer named "name" with a standard tokenizer and token filters
 *   for ASCII folding and lowercase conversion.
 * - Configures a custom analyzer named "english" with a standard tokenizer and additional
 *   token filters for ASCII folding, lowercase conversion, and Porter stemming.
 * - Configures a custom normalizer named "sort" with token filters for ASCII folding and
 *   lowercase conversion.
 * Note: This class is annotated with {@link SearchExtension} to indicate that it should be
 * discovered and used as a Hibernate Search extension during the application startup.
 */
@SearchExtension
public class AnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    /**
     * Configures custom analyzers and normalizers for Elasticsearch indexing.
     *
     * @param context The Elasticsearch analysis configuration context.
     */
    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        // Configure the "name" analyzer with a standard tokenizer and token filters
        // for ASCII folding and lowercase conversion.
        context.analyzer("name").custom()
                .tokenizer("standard")
                .tokenFilters("asciifolding", "lowercase");

        // Configure the "english" analyzer with a standard tokenizer and additional
        // token filters for ASCII folding, lowercase conversion, and Porter stemming.
        context.analyzer("english").custom()
                .tokenizer("standard")
                .tokenFilters("asciifolding", "lowercase", "porter_stem");

        // Configure the "sort" normalizer with token filters for ASCII folding and
        // lowercase conversion.
        context.normalizer("sort").custom()
                .tokenFilters("asciifolding", "lowercase");
    }
}