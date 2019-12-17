package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import kr.datastation.api.validator.EntityName;
import kr.datastation.api.vo.NewsNamedEntityList;
import kr.datastation.api.service.ElasticSearchService;
import kr.datastation.api.vo.NewsNamedEntitySummaryList;
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

    final ElasticSearchService elasticSearchService;

    @Autowired
    public NewsController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

//    @RequestMapping(value = "/topic/keyword/summary", method = RequestMethod.GET)
//    public List<Map<String, String>> getTopicSummary(String query, String from, String to,
//                                                     @RequestParam(defaultValue = "10") Integer size) {
//        return elasticSearchService.getTopicSummary(query, from, to, size);
//    }

    @RequestMapping(value = "/topic/keyword", method = RequestMethod.GET)
    public List<Map<String, String>> getTopic(String query, String from, String to,
                                              @RequestParam(defaultValue = "100") Integer size,
                                              @RequestParam(value = "sort", defaultValue = "desc") String sort) {
        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        return elasticSearchService.getTopic(query, from, to, size, sortOrder);
    }

    @RequestMapping(value = "/topic/related", method = RequestMethod.GET)
    public List<Map<String, String>> getRelatedTopic(String query, String from, String to,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(value = "sort", defaultValue = "desc") String sort) {
        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        return elasticSearchService.getRelatedTopic(query, from, to, size, sortOrder);
    }

//    @RequestMapping(value = "/topic/related/summary", method = RequestMethod.GET)
//    public List<Map<String, String>> getRelatedTopicSummary(String query, String from, String to,
//                                                            @RequestParam(defaultValue = "10") Integer size) {
//        return elasticSearchService.getRelatedTopicSummary(query, from, to, size);
//    }

    @RequestMapping(value = "/timeline/summary", method = RequestMethod.GET, produces = "application/json")
    public List<Long> getTimelineSummary(String query, String from, String to) {
        final List<String> intervals = Arrays.asList("1d", "1w", "1m");

        List<Long> resultList = new ArrayList<>();
//        for (String interval : intervals) { // TODO: async or logic
//            DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(interval);
//            Long docMeanFrequency = elasticSearchService.getDocMeanFrequency(query, from, to, dateHistogramInterval);
//            resultList.add(docMeanFrequency);
//        }
        resultList.add(10L);
        resultList.add(100L);
        resultList.add(1000L);
        return resultList;
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