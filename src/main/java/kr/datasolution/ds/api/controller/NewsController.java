package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.validator.EntityName;
import kr.datasolution.ds.api.validator.Interval;
import kr.datasolution.ds.api.domain.NewsNamedEntityList;
import kr.datasolution.ds.api.service.ElasticSearchService;
import kr.datasolution.ds.api.domain.NewsNamedEntitySummaryList;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/news")
@Api(value="/news")
@Validated
public class NewsController {

    @Autowired
    ElasticSearchService elasticSearchService;

    @RequestMapping(value = "/timeline/summary", method = RequestMethod.GET, produces = "application/json")
    public Long getTimelineSummary(String query, String startDate, String endDate,
                                   @RequestParam(value = "interval" , defaultValue = "1w") @Valid Interval interval) {

        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(interval.getInterval());
        return elasticSearchService.getDocMeanFrequency(query, startDate, endDate, dateHistogramInterval);
    }

    @RequestMapping(value = "/entity/summary", method = RequestMethod.GET)
    public NewsNamedEntitySummaryList getEntitySummary(@RequestParam @Valid EntityName entityName,
                                                       String startDate, String endDate, int size) {
        return elasticSearchService.getNamedEntitySummary(entityName.getEntityName(), startDate, endDate, size);
    }

    @RequestMapping(value = "/entity/name", method = RequestMethod.GET)
    public NewsNamedEntityList getEntityByName(@RequestParam @Valid EntityName entityName,
                                          String startDate, String endDate, int from, int size) {
        return elasticSearchService.getEntityByName(entityName.getEntityName(), startDate, endDate, from, size);
    }
}

// TODO: controlleradvice without requestparam