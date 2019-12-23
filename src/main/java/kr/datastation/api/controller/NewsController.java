package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import kr.datastation.api.util.HttpResponseCSVWriter;
import kr.datastation.api.validator.DateRequestParam;
import kr.datastation.api.validator.EntityName;
import kr.datastation.api.validator.EntitySortRequestParam;
import kr.datastation.api.vo.*;
import kr.datastation.api.service.ElasticSearchService;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
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
    public NewsNamedEntityList getEntityByName(@RequestParam String query,
                                               @RequestParam(value = "entity") @Valid EntityName entityName,
                                               @DateRequestParam(point = TimePoint.FROM) String from,
                                               @DateRequestParam(point = TimePoint.TO) String to,
                                               @RequestParam(defaultValue = "date.desc,entity.desc") List<String> sort, // TODO: validator
                                               @RequestParam int page, // from: (page-1)*size, size: size
                                               @RequestParam int size) {
        return elasticSearchService.getEntityByName(query, entityName.getEntityName(), from, to, page, size, sort);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211")
    })
    @RequestMapping(value = "/entity/name/download", method = RequestMethod.GET)
    public void downloadEntityByName(HttpServletResponse response,
                                     @RequestParam String query,
                                     @RequestParam(value = "entity") @Valid EntityName entityName,
                                     @DateRequestParam(point = TimePoint.FROM) String from,
                                     @DateRequestParam(point = TimePoint.TO) String to,
                                     @RequestParam(defaultValue = "date.desc,entity.desc") List<String> sort, // TODO: validator, swagger bug
                                     @RequestParam(defaultValue = "csv") String format) throws IOException { // TODO: file format
        final int page = 0, size = 100000;
        final List<String> headers = Arrays.asList("headline", "entity", "date");
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("entity.csv", response);
        httpResponseCsvWriter.setHeaders(headers);

        NewsNamedEntityList entityByName = elasticSearchService.getEntityByName(query, entityName.getEntityName(), from, to, page, size, sort);
        List<NewsNamedEntity> newsNamedEntityList = entityByName.getNewsNamedEntityList();
        newsNamedEntityList.forEach(element -> httpResponseCsvWriter.write(element.toCSV()));
        httpResponseCsvWriter.close();
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
    @RequestMapping(value = "/entity/spss/download", method = RequestMethod.GET)
    public void downloadEntitySPSS(HttpServletResponse response,
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
                                              @RequestParam(defaultValue = "100") int size) {
        final int bsize = 1;
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;
        return elasticSearchService.getTopic(query, from, to, size, bsize, histogramOrder, documentOrder);
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
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;
        return elasticSearchService.getRelatedTopic(query, from, to, size, bsize, histogramOrder, documentOrder);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/topn/download", method = RequestMethod.GET)
    public void downloadRelatedTopicTopN(HttpServletResponse response,
                                        @RequestParam String query,
                                        @DateRequestParam(point = TimePoint.FROM) String from,
                                        @DateRequestParam(point = TimePoint.TO) String to,
                                        @RequestParam(defaultValue = "100") int size, // the number of buckets
                                        @RequestParam(defaultValue = "10") int bsize) throws IOException { // each bucket size
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.KEY_DESC;

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

        final List<String> headers = Arrays.asList("name", "date");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + ", " + entry.get("date"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/spss/download", method = RequestMethod.GET)
    public void downloadRelatedTopicSPSS(HttpServletResponse response,
                                         @RequestParam String query,
                                         @DateRequestParam(point = TimePoint.FROM) String from,
                                         @DateRequestParam(point = TimePoint.TO) String to,
                                         @RequestParam(defaultValue = "100") int size) throws IOException {
        final int bsize = 1, maxSize = 100000;
        size = Math.min(maxSize, size);
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

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
                              @RequestParam(defaultValue = "100") int size) throws IOException {
        final int bsize = 1;
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("topic.csv", response);
        List<Map<String, String>> topic = elasticSearchService.getTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

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
