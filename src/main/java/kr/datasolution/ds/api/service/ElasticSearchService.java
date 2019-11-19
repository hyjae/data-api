package kr.datasolution.ds.api.service;

import com.google.gson.Gson;
import kr.datasolution.ds.api.domain.NewsNamedEntityList;
import kr.datasolution.ds.api.domain.NewsNamedEntitySummaryList;
import kr.datasolution.ds.api.domain.NewsNamedEntitySummary;
import kr.datasolution.ds.api.domain.NewsNamedEntity;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.avg.AvgBucketBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;


@Service
public class ElasticSearchService {

    private static final String NEWS_IDX = "news-*";

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Cacheable(value = "getDocMeanFrequency", key = "{#query, #startDate, #endDate, #dateHistogramInterval}")
    public Long getDocMeanFrequency(String query, String startDate, String endDate, DateHistogramInterval dateHistogramInterval) {

        DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder("date_hist")
                .field("written_time")
                .interval(dateHistogramInterval);
        AvgBucketBuilder avgBucketBuilder = new AvgBucketBuilder("avg_bucket")
                .setBucketsPaths("date_hist._count");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(startDate)
                        .to(endDate)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true))
                .size(0)
                .aggregation(dateHistogramBuilder)
                .aggregation(avgBucketBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();
        double avg = Double.parseDouble(searchResponse.getAggregations().get("avg_bucket").getProperty("value").toString());
        return Double.valueOf(avg).longValue();
    }

    public NewsNamedEntitySummaryList getNamedEntitySummary(String entityName, String startDate, String endDate, int size) {

        CardinalityBuilder cardinalityBuilder = AggregationBuilders.cardinality("num_total_terms").field(entityName);
        TermsBuilder termsBuilder = AggregationBuilders.terms(entityName).field(entityName).size(size);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(startDate)
                        .to(endDate)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true))
                .size(0)
                .sort(new FieldSortBuilder("written_time").order(SortOrder.DESC))
                .aggregation(termsBuilder)
                .aggregation(cardinalityBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        NewsNamedEntitySummaryList newsNamedEntitySummaryList = new NewsNamedEntitySummaryList();
        Aggregations aggregations = searchResponse.getAggregations();
        Terms terms = aggregations.get(entityName);
        InternalCardinality numTotalTerms = aggregations.get("num_total_terms");

        newsNamedEntitySummaryList.setNumTotalTerms(numTotalTerms.getValue());
        for (Terms.Bucket bucket : terms.getBuckets())
            newsNamedEntitySummaryList.add(new NewsNamedEntitySummary(bucket.getKeyAsString(), bucket.getDocCount()));
        return newsNamedEntitySummaryList;
    }

    public NewsNamedEntityList getEntityByName(String entityName, String startDate, String endDate, int from, int size) {

        final String[] includeFields = new String[] {"title", "link", "written_time", entityName};

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery(entityName));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(startDate)
                        .to(endDate)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true))
                .from(from)
                .size(size)
                .sort(new FieldSortBuilder("written_time").order(SortOrder.DESC))
                .query(boolQueryBuilder).fetchSource(includeFields, null);
        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        NewsNamedEntityList newsNamedEntityList = new NewsNamedEntityList();
        newsNamedEntityList.setTotalHits(searchResponse.getHits().getTotalHits());
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            NewsNamedEntity newsNamedEntity = new Gson().fromJson(hit.getSourceAsString(), NewsNamedEntity.class);
            newsNamedEntityList.add(newsNamedEntity);
        }
        return newsNamedEntityList;
    }
}


// TODO: snake