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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ElasticSearchService {

    private static final String NEWS_IDX = "news-*";

    final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public ElasticSearchService(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    /**
     * Steps:
     *  1. Aggregated by a list of topics; essentially this is the same as "GROUP BY topic"
     *      - This returns a list of topics + the number occurrence in documents
     *      - ex) "word", 15 => "word" shows up on 15 documents
     *  2. Sub-aggregated by a date and order by the number of occurrence
     *      - Each record now has a date on which a topic occurs at most.
     *      - ex) "word", 15, "20190101"
     */
    public List<Map<String, String>> getTopic(String query, String from, String to, int size, SortOrder sort) {
        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval("1d");
        TermsBuilder termsBuilder = AggregationBuilders
                .terms("agg")
                .field("topics")
                .size(size) // the number of buckets
                .subAggregation(AggregationBuilders
                        .dateHistogram("date_hist")
                        .format("yyyyMMdd")
                        .field("written_time")
                        .interval(dateHistogramInterval)
                        .order(Histogram.Order.COUNT_DESC));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from + "000000")
                        .to(to + "235959")
                        .format("yyyyMMddHHmmss")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .size(0)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        Terms terms = searchResponse.getAggregations().get("agg");

        final List<Map<String, String>> result = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, String> entity = new LinkedHashMap<>();
            Histogram histogram = bucket.getAggregations().get("date_hist");
            entity.put("date", histogram.getBuckets().get(0).getKeyAsString()); // select one with the highest count
            entity.put("name", bucket.getKeyAsString());
            entity.put("count", String.valueOf(bucket.getDocCount()));
            result.add(entity);
        }

        // only if you want to order them by date field
//        final int i = sort.toString().equalsIgnoreCase("desc") ? -1 : 1;
//        result.sort((o1, o2) -> {
//            if (o1.get("date").compareTo(o2.get("date")) > 0) return i;
//            else if (o1.get("date").compareTo(o2.get("date")) < 0) return i * (-1);
//            else return 0;
//        });
        return result;
    }

    public List<Map<String, String>> getRelatedTopic(String query, String from, String to, int size, int bsize, SortBy sortBy) {
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
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from + "000000")
                        .to(to + "235959")
                        .format("yyyyMMddHHmmss")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .size(0)
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        Terms terms = searchResponse.getAggregations().get("agg");

        final List<Map<String, String>> result = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            Map<String, String> entity = new LinkedHashMap<>();
            Histogram histogram = bucket.getAggregations().get("date_hist");
            bsize = Math.min(histogram.getBuckets().size(), bsize);
            for (int i = 0; i < bsize; ++i) {
                entity.put("date", histogram.getBuckets().get(i).getKeyAsString());
                entity.put("name", bucket.getKeyAsString());
                entity.put("count", String.valueOf(bucket.getDocCount()));
                result.add(entity);
            }
        }
        if (sortBy == SortBy.DATE) {
            // only if you want to order them by date field
            final int i = sort.toString().equalsIgnoreCase("desc") ? -1 : 1;
            result.sort((o1, o2) -> {
                if (o1.get("date").compareTo(o2.get("date")) > 0) return i;
                else if (o1.get("date").compareTo(o2.get("date")) < 0) return i * (-1);
                else return 0;
            });
        }
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

    public List<NewsNamedEntityCSVObject> getNamedEntityCSVObject(String query, List<String> namedEntityList, String from, String to, int size) {
        final int max = 1000;
        size = Math.min(max, size);

        List<NewsNamedEntityCSVObject> resultList = new ArrayList<>();
        for (String entityName : namedEntityList)
            resultList.addAll(getNamedEntity(query, entityName, from, to, size));
        return resultList;
    }

    private List<NewsNamedEntityCSVObject> getNamedEntity(String query, String entityName, String from, String to, int size) {
        TermsBuilder termsBuilder = AggregationBuilders.terms(entityName).field(entityName).size(size); // default size 10

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .size(0)
                .sort(new FieldSortBuilder("written_time").order(SortOrder.DESC))
                .aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        NewsNamedEntitySummaryList newsNamedEntitySummaryList = new NewsNamedEntitySummaryList();
        newsNamedEntitySummaryList.setEntityName(entityName);
        Aggregations aggregations = searchResponse.getAggregations();
        Terms terms = aggregations.get(entityName);

        List<NewsNamedEntityCSVObject> newsNamedEntityCSVObjectList = new ArrayList<>();
        for (Terms.Bucket bucket : terms.getBuckets())
            newsNamedEntityCSVObjectList.add(new NewsNamedEntityCSVObject(bucket.getKeyAsString(), entityName, bucket.getDocCount()));
        return newsNamedEntityCSVObjectList;
    }

    public NewsNamedEntitySummaryList getNamedEntitySummary(String query, String entityName, String from, String to, int size) {

        CardinalityBuilder cardinalityBuilder = AggregationBuilders.cardinality("num_total_terms").field(entityName);
        TermsBuilder termsBuilder = AggregationBuilders.terms(entityName).field(entityName).size(size); // default size 10

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
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

    public NewsNamedEntityList getEntityByName(String entityName, String from, String to, int page, int size) {

        final String[] includeFields = new String[] {"title", "link", "written_time", entityName};

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery(entityName));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true)))
                .from(page)
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

    public List<TimeLineChart> getTimeLine(String query, String from, String to, String interval) throws ParseException {
        final DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(interval);
        final NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withIndices(NEWS_IDX)
                .withQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.rangeQuery("written_time")
                                .from(from + "000000")
                                .to(to + "235959")
                                .format("yyyyMMddHHmmss")
                                .includeLower(true)
                                .includeUpper(true))
                        .must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .withPageable(new PageRequest(0, 1))
                .addAggregation(
                        AggregationBuilders.dateHistogram("agg").format("yyyyMMdd").field("written_time")
                                .interval(dateHistogramInterval)
                                .order(Histogram.Order.KEY_ASC)
                                .subAggregation(AggregationBuilders.terms("agg").field("actions").size(1))
                );
        final SearchQuery searchQuery = queryBuilder.build();
        final Aggregations aggregations = elasticsearchTemplate.query(searchQuery, SearchResponse::getAggregations);
        final Histogram agg = aggregations.get("agg");
        return makeTimeLineChart(agg);
    }

    private List<TimeLineChart> makeTimeLineChart(Histogram agg) throws ParseException {
        SimpleDateFormat toDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat fromDateFormat = new SimpleDateFormat("yyyyMMdd");

        final List<TimeLineChart> result = new ArrayList<>();
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Terms terms = entry.getAggregations().get("agg");
            if (terms.getBuckets().size() > 0) {
                String action = terms.getBuckets().get(0).getKeyAsString();
                result.add(new TimeLineChart(toDateFormat.format(fromDateFormat.parse(entry.getKeyAsString())), entry.getDocCount(), action));
            }
        }
        return result;
    }

    public List<Map<String, String>> getDocumentCount(String query, String from, String to, String interval) {
        final DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(interval);
        DateHistogramBuilder dateHistogramBuilder = AggregationBuilders
                .dateHistogram("date_hist")
                .format("yyyyMMdd")
                .field("written_time")
                .interval(dateHistogramInterval)
                .order(Histogram.Order.KEY_ASC);

        // Note: Use boolQuery().must() to apply range query, otherwise you will get the entire range dataset
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from + "000000")
                        .to(to + "235959")
                        .format("yyyyMMddHHmmss")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .size(0)
                .sort("written_time", SortOrder.DESC)
                .aggregation(dateHistogramBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        Histogram dateHist = searchResponse.getAggregations().get("date_hist");

        final List<Map<String, String>> result = new ArrayList<>();
        for (Histogram.Bucket bucket : dateHist.getBuckets()) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("yyyymm", bucket.getKeyAsString().substring(0, 6));
            entry.put("doccnt", String.valueOf(bucket.getDocCount()));
            result.add(entry);
        }
        return result;
    }
}
