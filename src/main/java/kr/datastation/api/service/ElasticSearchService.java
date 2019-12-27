package kr.datastation.api.service;

import com.google.gson.Gson;
import kr.datastation.api.util.CommonUtils;
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
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

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

    public List<Map<String, String>> getTopic(String query, String from, String to, int size, int bsize,
                                              Histogram.Order histogramOrder, DocumentOrder documentOrder) {
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
                        .order(histogramOrder));

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
        if (bsize > 1) { // documents in a bucket are ordered by count desc by default
            // only if you want to order them by date field
            final int i = documentOrder.getDocumentOrder().equalsIgnoreCase("key_desc") ? -1 : 1;
            result.sort((o1, o2) -> {
                if (o1.get("date").compareTo(o2.get("date")) > 0) return i;
                else if (o1.get("date").compareTo(o2.get("date")) < 0) return i * (-1);
                else return 0;
            });
        }
        return result;
    }

    public List<Map<String, String>> getDailyRank(String query, String field, String from, String to, int size,
                                                  int bsize, Histogram.Order histogramOrder) {
        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval("1d");
        DateHistogramBuilder dateHistogramBuilder = AggregationBuilders
                .dateHistogram("date_hist")
                .format("yyyyMMdd")
                .field("written_time")
                .interval(dateHistogramInterval)
                .order(histogramOrder).subAggregation(AggregationBuilders
                        .terms("agg")
                        .field(field)
                        .size(size));

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
                .aggregation(dateHistogramBuilder);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();

        List<Map<String, String>> result = new ArrayList<>();
        Histogram histogram = searchResponse.getAggregations().get("date_hist");
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            String date = bucket.getKeyAsString();
            Terms terms = bucket.getAggregations().get("agg");
            long min = Math.min(terms.getBuckets().size(), bsize);
            List<Terms.Bucket> buckets = terms.getBuckets();
            for (int i = 0; i < min; ++i) {
                Map<String, String> entity = new LinkedHashMap<>();
                entity.put("date", date);
                entity.put("name", buckets.get(i).getKeyAsString());
                entity.put("count", String.valueOf(buckets.get(i).getDocCount()));
                result.add(entity);
            }
        }
        return result;
    }


    public List<Map<String, String>> getRelatedTopic(String query, String from, String to, int size, int bsize,
                                                     Histogram.Order histogramOrder, DocumentOrder documentOrder) {
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
                        .order(histogramOrder));

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
        if (bsize > 1) { // documents in a bucket are ordered by count desc by default
            // only if you want to order them by date field
            final int i = documentOrder.getDocumentOrder().equalsIgnoreCase("key_desc") ? -1 : 1;
            result.sort((o1, o2) -> {
                if (o1.get("date").compareTo(o2.get("date")) > 0) return i;
                else if (o1.get("date").compareTo(o2.get("date")) < 0) return i * (-1);
                else return 0;
            });
        }
        return result;
    }

    public List<Double> getDocFrequency(String query, String from, String to) {
        final String format = "yyyyMMdd";
        List<Double> resultList = new ArrayList<>();

        Long docCount = getDocCount(query, from, to);
        long days = CommonUtils.getTimeDiffAsDay(from, to, format);

        List<Double> denominators = new ArrayList<>();
        denominators.add((double) days);
        denominators.add(days / 7.0);
        denominators.add(days / 365.0);

        try {
            for (Double denominator : denominators)
                resultList.add(docCount / denominator);
        } catch (ArithmeticException e) {
            resultList.add(null);
        }
        return resultList;
    }

    private Long getDocCount(String query, String from, String to) {
        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .size(0);

        SearchRequest searchRequest = new SearchRequest(NEWS_IDX).source(searchSourceBuilder);
        SearchResponse searchResponse = elasticsearchTemplate.getClient().search(searchRequest).actionGet();
        return searchResponse.getHits().totalHits();
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

    private List<SortBuilder> getEntityByNameHelper(String entityName, List<String> entitySortList) {
        final String sortScript = "doc['" + entityName + "'].values.size()";

        List<SortBuilder> sortBuilderList = new ArrayList<>();
        for (String entitySort: entitySortList)
            if (entitySort.equalsIgnoreCase(EntitySort.DATE_ASC.getEntityOrder())) {
                sortBuilderList.add(new FieldSortBuilder("written_time").order(SortOrder.ASC));
            } else if (entitySort.equalsIgnoreCase(EntitySort.DATE_DESC.getEntityOrder())) {
                sortBuilderList.add(new FieldSortBuilder("written_time").order(SortOrder.DESC));
            } else if (entitySort.equalsIgnoreCase(EntitySort.ENTITY_ASC.getEntityOrder())) {
                sortBuilderList.add(new ScriptSortBuilder(new Script(sortScript), "number").order(SortOrder.ASC));
            } else {
                sortBuilderList.add(new ScriptSortBuilder(new Script(sortScript), "number").order(SortOrder.DESC));
            }
        return sortBuilderList;
    }

    public NewsNamedEntityList getEntityByName(String query, String entityName, String from, String to, int page, int size, List<String> entitySortList) {

        final String[] includeFields = new String[] {"title", "link", "written_time", entityName};
        final List<SortBuilder> entityByNameHelper = getEntityByNameHelper(entityName, entitySortList);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery(entityName));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("written_time")
                        .from(from)
                        .to(to)
                        .format("yyyyMMdd")
                        .includeLower(true)
                        .includeUpper(true)).must(QueryBuilders.queryStringQuery(query)
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND)
                        .defaultField("content")))
                .from(page)
                .size(size)
                .sort(entityByNameHelper.get(0)) // TODO: poor performance
                .sort(entityByNameHelper.get(1))
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
