package kr.datasolution.ds.api.service;

import com.google.gson.Gson;
import io.swagger.models.auth.In;
import kr.datasolution.ds.api.domain.*;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ElasticSearchService {

    private static final String NEWS_IDX = "news-*";

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    public List<Map<String, Integer>> getTopics(String query, String from, String to, Integer size) {
        TermsBuilder termsBuilder = AggregationBuilders.terms("agg").field("topics").size(size);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(from + "000000")
                        .to(to + "235959")
                        .format("yyyyMMddHHmmss")
                        .includeLower(true)
                        .includeUpper(true))
                .query(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content"))
                .size(0)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        List<Map<String, Integer>> topicsRanked = new ArrayList<>();
        Terms terms = searchResponse.getAggregations().get("agg");

        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, Integer> topic = new HashMap<String, Integer>() {{
                put("text", bucket.getKey());
                put("value", (int) bucket.getDocCount());
            }};
            topicsRanked.add(topic);
        }
        return topicsRanked;
    }

    public NewsRelatedTopicList getRelatedTopics(String query, String startDate, String endDate,
                                                 DateHistogramInterval dateHistogramInterval, int size, SortOrder sort) {
        final DateHistogramBuilder dateHistogramBuilder = AggregationBuilders
                .dateHistogram("date_hist")
                .format("yyyyMMdd")
                .field("written_time")
                .interval(dateHistogramInterval)
                .order(Histogram.Order.COUNT_DESC)
                .subAggregation(AggregationBuilders
                        .terms("date_hist")
                        .field("actions")
                        .size(size));

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(startDate)
                        .to(endDate)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true))
                .query(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content"))
                .size(0)
                .sort(new FieldSortBuilder("written_time"))
                .aggregation(dateHistogramBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        Aggregations aggregations = searchResponse.getAggregations();
        Aggregation date_hist = aggregations.get("date_hist");

        NewsRelatedTopicList newsRelatedTopicList = new NewsRelatedTopicList();
//        for (Terms.Bucket bucket : terms.getBuckets()) {
//            String keyAsString = bucket.getKeyAsString();
//            long docCount = bucket.getDocCount();
//        }
//        return newsRelatedTopicList;
        return null;
    }

    @Cacheable(value = "getDocMeanFrequency", key = "{#query, #from, #to, #interval}")
    public Long getDocMeanFrequency(String query, String from, String to, DateHistogramInterval interval) {
        DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder("date_hist")
                .field("written_time")
                .interval(interval);
        AvgBucketBuilder avgBucketBuilder = new AvgBucketBuilder("avg_bucket")
                .setBucketsPaths("date_hist._count");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true))
                .query(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content"))
                .size(0)
                .aggregation(dateHistogramBuilder)
                .aggregation(avgBucketBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();
        double avg = Double.parseDouble(searchResponse.getAggregations().get("avg_bucket").getProperty("value").toString());
        return Double.valueOf(avg).longValue();
    }

    public NewsNamedEntitySummaryList getNamedEntitySummary(String query, String entityName, String from, String to, int size) {

        CardinalityBuilder cardinalityBuilder = AggregationBuilders.cardinality("num_total_terms").field(entityName);
        TermsBuilder termsBuilder = AggregationBuilders.terms(entityName).field(entityName).size(size);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true))
                .query(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content"))
                .size(0)
                .sort(new FieldSortBuilder("written_time").order(SortOrder.DESC))
                .aggregation(termsBuilder)
                .aggregation(cardinalityBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        NewsNamedEntitySummaryList newsNamedEntitySummaryList = new NewsNamedEntitySummaryList();
        newsNamedEntitySummaryList.setEntityName(entityName);
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
