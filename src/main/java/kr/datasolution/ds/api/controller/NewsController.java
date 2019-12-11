package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.domain.NewsRelatedTopicList;
import kr.datasolution.ds.api.validator.EntityName;
import kr.datasolution.ds.api.validator.Interval;
import kr.datasolution.ds.api.domain.NewsNamedEntityList;
import kr.datasolution.ds.api.service.ElasticSearchService;
import kr.datasolution.ds.api.domain.NewsNamedEntitySummaryList;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/news")
@Api(value="/news")
@Validated
public class NewsController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public List<Map<String, Integer>> getTopic(String query, String from, String to,
                                               @RequestParam(defaultValue = "100") Integer size) {
        return elasticSearchService.getTopics(query, from, to, size);
    }

    @RequestMapping(value = "/topic/related", method = RequestMethod.GET)
    public NewsRelatedTopicList getRelatedTopic(String query, String from, String to,
                                                @RequestParam(value = "interval", defaultValue = "1d") @Valid Interval interval,
                                                int size, @RequestParam(value = "sort", defaultValue = "DESC") String sort) {
        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(interval.getInterval());
        return elasticSearchService.getRelatedTopics(query, from, to, dateHistogramInterval, size, sortOrder);
    }

    @RequestMapping(value = "/timeline/summary", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Long> getTimelineSummary(String query, String from, String to) {
        final List<String> intervals = Arrays.asList("1d", "1w", "1m");

        Map<String, Long> resultMap = new HashMap<>();
        for (String interval : intervals) { // TODO: async or logic
            DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(interval);
            Long docMeanFrequency = elasticSearchService.getDocMeanFrequency(query, from, to, dateHistogramInterval);
            resultMap.put(interval, docMeanFrequency);
        }
        return resultMap;
    }

    @RequestMapping(value = "/entity/summary", method = RequestMethod.GET)
    public List<NewsNamedEntitySummaryList> getEntitySummary(String query, String from, String to, int size) {
        final List<String> entityNames = Arrays.asList("locationNamedEntity", "organizationNamedEntity", "personNamedEntity", "etcNamedEntity");

        List<NewsNamedEntitySummaryList> resultList = new ArrayList<>();
        for (String entityName : entityNames) // TODO: async
            resultList.add(elasticSearchService.getNamedEntitySummary(query, entityName, from, to, size));
        return resultList;
    }

    // from: (page-1)*size, size: size
    @RequestMapping(value = "/entity/name", method = RequestMethod.GET)
    public NewsNamedEntityList getEntityByName(@RequestParam(value = "entity") @Valid EntityName entityName,
                                               String from, String to, int page, int size) {
        return elasticSearchService.getEntityByName(entityName.getEntityName(), from, to, page, size);
    }
}

// TODO: controlleradvice without requestparam