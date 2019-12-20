package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datastation.api.util.HttpResponseCSVWriter;
import kr.datastation.api.validator.DateRequestParam;
import kr.datastation.api.validator.EntityName;
import kr.datastation.api.vo.*;
import kr.datastation.api.service.ElasticSearchService;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.util.*;


@RestController
@RequestMapping("/news")
@Api(value="/news")
@Validated
public class NewsController {

    final ElasticSearchService elasticSearchService;
    final List<String> entityNames = Arrays.asList(
            "locationNamedEntity", "organizationNamedEntity", "personNamedEntity", "etcNamedEntity", "totalNamedEntity");

    @Autowired
    public NewsController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/entity/name", method = RequestMethod.GET)
    public NewsNamedEntityList getEntityByName(@RequestParam(value = "entity") @Valid EntityName entityName,
                                               @DateRequestParam(point = TimePoint.FROM) String from,
                                               @DateRequestParam(point = TimePoint.FROM) String to,
                                               @RequestParam int page,
                                               @RequestParam int size) {
        // from: (page-1)*size, size: size
        return elasticSearchService.getEntityByName(entityName.getEntityName(), from, to, page, size);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/entity/summary", method = RequestMethod.GET)
    public List<NewsNamedEntitySummaryList> getEntitySummary(@RequestParam String query,
                                                             @DateRequestParam(point = TimePoint.FROM) String from,
                                                             @DateRequestParam(point = TimePoint.TO) String to,
                                                             @RequestParam(value = "size", defaultValue = "100") int size) {
        List<NewsNamedEntitySummaryList> resultList = new ArrayList<>();
        for (String entityName : entityNames) // TODO: async
            resultList.add(elasticSearchService.getNamedEntitySummary(query, entityName, from, to, size));
        return resultList;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/entity/download", method = RequestMethod.GET)
    public void downloadFullEntity(HttpServletResponse response,
                                   @RequestParam String query,
                                   @DateRequestParam(point = TimePoint.FROM) String from,
                                   @DateRequestParam(point = TimePoint.TO) String to,
                                   @RequestParam(value = "size", defaultValue = "1000") int size,
                                   @RequestParam(defaultValue = "csv") String format) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("entity.csv", response);

        final List<String> headers = Arrays.asList("entity", "entitykind", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);

        List<NewsNamedEntityCSVObject> namedEntityList = elasticSearchService.getNamedEntityCSVObject(query, entityNames, from, to, size);
        namedEntityList.forEach(element -> httpResponseCsvWriter.write(element.toCSV()));
        httpResponseCsvWriter.close();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/topic", method = RequestMethod.GET)
    public List<Map<String, String>> getTopic(@RequestParam String query,
                                              @DateRequestParam(point = TimePoint.FROM) String from,
                                              @DateRequestParam(point = TimePoint.TO) String to,
                                              @RequestParam(defaultValue = "100") int size,
                                              @RequestParam(value = "sort", defaultValue = "desc") String sort) {
        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        return elasticSearchService.getTopic(query, from, to, size, sortOrder);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related", method = RequestMethod.GET)
    public List<Map<String, String>> getRelatedTopic(@RequestParam String query,
                                                     @DateRequestParam(point = TimePoint.FROM) String from,
                                                     @DateRequestParam(point = TimePoint.TO) String to,
                                                     @RequestParam(defaultValue = "100") int size) {
        final int bsize = 1;
        return elasticSearchService.getRelatedTopic(query, from, to, size, bsize, SortBy.COUNT);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/download", method = RequestMethod.GET)
    public void downloadRelatedTopicTop(HttpServletResponse response,
                                        @RequestParam String query,
                                        @DateRequestParam(point = TimePoint.FROM) String from,
                                        @DateRequestParam(point = TimePoint.TO) String to,
                                        @RequestParam(defaultValue = "100") int size, // the number of buckets
                                        @RequestParam(defaultValue = "1") int bsize) throws IOException { // each bucket size
        // Note: sort by Histogram.Order.COUNT_DESC by default for each bucket
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, bsize, SortBy.DATE);

        final List<String> headers = Arrays.asList("name", "date");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + ", " + entry.get("date"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/download", method = RequestMethod.GET)
    public void downloadRelatedTopic(HttpServletResponse response,
                                     @RequestParam String query,
                                     @DateRequestParam(point = TimePoint.FROM) String from,
                                     @DateRequestParam(point = TimePoint.TO) String to,
                                     @RequestParam(defaultValue = "100") int size,
                                     @RequestParam(value = "sort", defaultValue = "desc") String sort) throws IOException {
        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, sortOrder);

        final List<String> headers = Arrays.asList("name", "date");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + ", " + entry.get("date"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/download", method = RequestMethod.GET)
    public void downloadRelatedTopicSPSS(HttpServletResponse response,
                                         @RequestParam String query,
                                         @DateRequestParam(point = TimePoint.FROM) String from,
                                         @DateRequestParam(point = TimePoint.TO) String to,
                                         @RequestParam(defaultValue = "100") int size,
                                         @RequestParam(value = "sort", defaultValue = "desc") String sort) throws IOException {
        final int maxSize = 100000;
        size = Math.min(maxSize, size);

        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, sortOrder);

        final List<String> headers = Arrays.asList("relword", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + ", " + entry.get("count"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/topic/download", method = RequestMethod.GET)
    public void downloadTopic(HttpServletResponse response,
                              @RequestParam String query,
                              @DateRequestParam(point = TimePoint.FROM) String from,
                              @DateRequestParam(point = TimePoint.TO) String to,
                              @RequestParam(defaultValue = "100") int size,
                              @RequestParam(value = "sort", defaultValue = "desc") String sort) throws IOException {
        SortOrder sortOrder = SortOrder.valueOf(sort.toUpperCase());
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("topic.csv", response);
        List<Map<String, String>> topic = elasticSearchService.getTopic(query, from, to, size, sortOrder);

        final List<String> headers = Arrays.asList("subject", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : topic)
            httpResponseCsvWriter.write(entry.get("name") + ", " + entry.get("count"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/timeline/summary", method = RequestMethod.GET, produces = "application/json")
    public List<Long> getTimelineSummary(@RequestParam String query,
                                         @DateRequestParam(point = TimePoint.FROM) String from,
                                         @DateRequestParam(point = TimePoint.TO) String to) {
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

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/timeline", method = RequestMethod.GET)
    public List<TimeLineChart> getTimeLineChart(@RequestParam String query,
                                                @DateRequestParam(point = TimePoint.FROM) String from,
                                                @DateRequestParam(point = TimePoint.TO) String to) throws ParseException {
        final String interval = "1d";
        return elasticSearchService.getTimeLine(query, from, to, interval);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/count/download", method = RequestMethod.GET)
    public void getDocumentCount(HttpServletResponse response, @RequestParam String query,
                                 @RequestParam(defaultValue = "csv") String format,
                                 @RequestParam(defaultValue = "1M") String interval,
                                 @DateRequestParam(point = TimePoint.FROM) String from,
                                 @DateRequestParam(point = TimePoint.TO) String to) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("count.csv", response);

        List<Map<String, String>> documentCount = elasticSearchService.getDocumentCount(query, from, to, interval);
        final List<String> headers = Arrays.asList("yyyymm", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : documentCount)
            httpResponseCsvWriter.write(entry.get("yyyymm") + ", " + entry.get("doccnt"));
        httpResponseCsvWriter.close();
    }
}

// TODO: controlleradvice without requestparam
// TODO: swagger doc
