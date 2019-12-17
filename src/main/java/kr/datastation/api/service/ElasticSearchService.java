package kr.datastation.api.service;

import com.google.gson.Gson;
import kr.datastation.api.vo.*;
import kr.datastation.api.vo.NewsNamedEntity;
import kr.datastation.api.vo.NewsNamedEntityList;
import kr.datastation.api.vo.NewsNamedEntitySummary;
import kr.datastation.api.vo.NewsNamedEntitySummaryList;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
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

import java.util.*;


@Service
public class ElasticSearchService {

    private static final String NEWS_IDX = "news-*";

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    public List<Map<String, String>> getTopicSummary(String query, String from, String to, Integer size) {
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
                .sort("written_time", SortOrder.DESC)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        final List<Map<String, String>> result = new ArrayList<>();
        Terms terms = searchResponse.getAggregations().get("agg");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, String> entity = new LinkedHashMap<>();
            entity.put("data", String.valueOf(bucket.getKey()));
            result.add(entity);
        }
        return result;
    }

    public List<Map<String, String>> getTopic(String query, String from, String to, Integer size, SortOrder sort) {
        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval("1d");
        TermsBuilder termsBuilder = AggregationBuilders
                .terms("agg")
                .field("topics")
                .size(size)
                .subAggregation(AggregationBuilders
                        .dateHistogram("date_hist")
                        .format("yyyyMMdd")
                        .field("written_time")
                        .interval(dateHistogramInterval)
                        .order(Histogram.Order.COUNT_DESC));

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
                .sort("written_time", SortOrder.DESC)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        Terms terms = searchResponse.getAggregations().get("agg");

        final List<Map<String, String>> result = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, String> entity = new LinkedHashMap<>();
            Histogram histogram = bucket.getAggregations().get("date_hist");
            entity.put("date", histogram.getBuckets().get(0).getKeyAsString());
            entity.put("name", bucket.getKeyAsString());
            result.add(entity);
        }

        final int i = sort.toString().equalsIgnoreCase("desc") ? -1 : 1;
        result.sort((o1, o2) -> {
            if (o1.get("date").compareTo(o2.get("date")) > 0) return i;
            else if (o1.get("date").compareTo(o2.get("date")) < 0) return i * (-1);
            else return 0;
        });
        return result;
    }

    public List<Map<String, String>> getRelatedTopicSummary(String query, String from, String to, Integer size) {
        TermsBuilder termsBuilder = AggregationBuilders.terms("agg").field("actions").size(size);

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
                .sort("written_time", SortOrder.DESC)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        final List<Map<String, String>> result = new ArrayList<>();
        Terms terms = searchResponse.getAggregations().get("agg");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, String> entity = new LinkedHashMap<>();
            entity.put("data", String.valueOf(bucket.getKey()));
            result.add(entity);
        }
        return result;
    }

    public List<Map<String, String>> getRelatedTopic(String query, String from, String to, Integer size, SortOrder sort) {
        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval("1d");
        TermsBuilder termsBuilder = AggregationBuilders
                .terms("agg")
                .field("actions")
                .size(size)
                .subAggregation(AggregationBuilders
                        .dateHistogram("date_hist")
                        .format("yyyyMMdd")
                        .field("written_time")
                        .interval(dateHistogramInterval)
                        .order(Histogram.Order.COUNT_DESC));

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
                .sort("written_time", SortOrder.DESC)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        Terms terms = searchResponse.getAggregations().get("agg");

        final List<Map<String, String>> result = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, String> entity = new LinkedHashMap<>();
            Histogram histogram = bucket.getAggregations().get("date_hist");
            entity.put("date", histogram.getBuckets().get(0).getKeyAsString());
            entity.put("name", bucket.getKeyAsString());
            result.add(entity);
        }

        final int i = sort.toString().equalsIgnoreCase("desc") ? -1 : 1;
        result.sort((o1, o2) -> {
            if (o1.get("date").compareTo(o2.get("date")) > 0) return i;
            else if (o1.get("date").compareTo(o2.get("date")) < 0) return i * (-1);
            else return 0;
        });
        return result;
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
