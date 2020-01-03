package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datastation.api.util.HttpResponseCSVWriter;
import kr.datastation.api.validator.DateRequestParam;
import kr.datastation.api.validator.EntityName;
import kr.datastation.api.vo.*;
import kr.datastation.api.service.ElasticSearchService;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.joda.time.DateTime;
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
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/news")
@Api(value="/news")
@Validated
public class NewsController {

    final ElasticSearchService elasticSearchService;
    final List<String> entityNames = Arrays.asList(
            "locationNamedEntity", "organizationNamedEntity", "etcNamedEntity", "totalNamedEntity", "personNamedEntity");

    @Autowired
    public NewsController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/entity", method = RequestMethod.GET)
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
    @RequestMapping(value = "/entity/download", method = RequestMethod.GET)
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
                                   @RequestParam(value = "size", defaultValue = "1000") int size) throws IOException {
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
        final int bsize = 1; // size of each bucket
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;
        return elasticSearchService.getTopic(query, from, to, size, bsize, histogramOrder, documentOrder);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/topic/spss/download", method = RequestMethod.GET)
    public void downloadTopicSPSS(HttpServletResponse response,
                                  @RequestParam String query,
                                  @DateRequestParam(point = TimePoint.FROM) String from,
                                  @DateRequestParam(point = TimePoint.TO) String to,
                                  @RequestParam(defaultValue = "1000") int size) throws IOException {
        final int bsize = 1, maxSize = 100000;
        size = Math.min(maxSize, size);
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("topic.csv", response);
        List<Map<String, String>> topic = elasticSearchService.getTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

        final List<String> headers = Arrays.asList("subject", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : topic)
            httpResponseCsvWriter.write(entry.get("name") + "," + entry.get("count"));
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
                              @RequestParam(defaultValue = "10000") int size) throws IOException {
        final int bsize = 1;
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("topic.csv", response);
        List<Map<String, String>> topic = elasticSearchService.getTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

        final List<String> headers = Arrays.asList("subject", "date");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : topic)
            httpResponseCsvWriter.write(entry.get("name") + "," + entry.get("date"));
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
    @RequestMapping(value = "/related/spss/download", method = RequestMethod.GET)
    public void downloadRelatedTopicSPSS(HttpServletResponse response,
                                         @RequestParam String query,
                                         @DateRequestParam(point = TimePoint.FROM) String from,
                                         @DateRequestParam(point = TimePoint.TO) String to,
                                         @RequestParam(defaultValue = "100") int size) throws IOException {
        final int bsize = 1, maxSize = 100000;
        size = Math.min(maxSize, size);
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC; // final output
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC; // sub-bucket order

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

        final List<String> headers = Arrays.asList("relword", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + "," + entry.get("count"));
    }

    /*
     * The size parameter can be set to define how many term buckets should be returned out of the overall terms list.
     * By default, the node coordinating the search process will request each shard to provide its own top size term
     * buckets and once all shards respond, it will reduce the results to the final list that will then be returned to the client.
     * */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/download", method = RequestMethod.GET)
    public void downloadRelatedTopic(HttpServletResponse response,
                                     @RequestParam String query,
                                     @DateRequestParam(point = TimePoint.FROM) String from,
                                     @DateRequestParam(point = TimePoint.TO) String to,
                                     @RequestParam(defaultValue = "10000") int size) throws IOException {
        final int bsize = 1;
        final DocumentOrder documentOrder = DocumentOrder.KEY_DESC;
        final Histogram.Order histogramOrder = Histogram.Order.COUNT_DESC;

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getRelatedTopic(query, from, to, size, bsize, histogramOrder, documentOrder);

        final List<String> headers = Arrays.asList("name", "date");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + "," + entry.get("date"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/related/rank/download", method = RequestMethod.GET)
    public void downloadRelatedRank(HttpServletResponse response,
                                    @RequestParam String query,
                                    @DateRequestParam(point = TimePoint.FROM) String from,
                                    @DateRequestParam(point = TimePoint.TO) String to,
                                    @RequestParam(defaultValue = "csv") String format,
                                    @RequestParam(defaultValue = "10") int size) throws IOException {
        final String field = "actions";
        final Histogram.Order histogramOrder = Histogram.Order.KEY_DESC;
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("related.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getDailyRank(query, field, from, to, size, histogramOrder);

        final List<String> headers = Arrays.asList("name", "date", "count");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + "," + entry.get("date") + "," + entry.get("count"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/topic/rank/download", method = RequestMethod.GET)
    public void downloadTopicRank(HttpServletResponse response,
                                     @RequestParam String query,
                                     @DateRequestParam(point = TimePoint.FROM) String from,
                                     @DateRequestParam(point = TimePoint.TO) String to,
                                     @RequestParam(defaultValue = "10000") int size) throws IOException {
        final String field = "topic";
        final Histogram.Order histogramOrder = Histogram.Order.KEY_DESC;
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("topic.csv", response);
        List<Map<String, String>> relatedTopic = elasticSearchService.getDailyRank(query, field, from, to, size, histogramOrder);

        final List<String> headers = Arrays.asList("name", "date", "count");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : relatedTopic)
            httpResponseCsvWriter.write(entry.get("name") + "," + entry.get("date") + "," + entry.get("count"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/timeline/summary", method = RequestMethod.GET, produces = "application/json")
    public List<Double> getTimelineSummary(@RequestParam String query,
                                           @DateRequestParam(point = TimePoint.FROM) String from,
                                           @DateRequestParam(point = TimePoint.TO) String to) {
//        final String from = "20170101";
//        final String to = new SimpleDateFormat("yyyyMMdd").format(DateTime.now().toDate());
        return elasticSearchService.getDocFrequency(query, from, to);
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
//        final String from = "20170101";
//        final String to = new SimpleDateFormat("yyyyMMdd").format(DateTime.now().toDate());
        return elasticSearchService.getTimeLine(query, from, to, interval);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/timeline/download", method = RequestMethod.GET)
    public void downloadTimeLineChart(HttpServletResponse response,
                                      @RequestParam String query,
                                      @DateRequestParam(point = TimePoint.FROM) String from,
                                      @DateRequestParam(point = TimePoint.TO) String to) throws ParseException, IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("timechart.csv", response);

        final String interval = "1d";
//        final String from = "20170101";
//        final String to = new SimpleDateFormat("yyyyMMdd").format(DateTime.now().toDate());
        List<TimeLineChart> timeLineList = elasticSearchService.getTimeLine(query, from, to, interval);

        final List<String> headers = Arrays.asList("date", "event", "value");
        httpResponseCsvWriter.setHeaders(headers);
        for (TimeLineChart timeLineChart : timeLineList)
            httpResponseCsvWriter.write(timeLineChart.toCSV());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/count/spss/download", method = RequestMethod.GET)
    public void getDocumentCount(HttpServletResponse response, @RequestParam String query,
                                 @DateRequestParam(point = TimePoint.FROM) String from,
                                 @DateRequestParam(point = TimePoint.TO) String to) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("count.csv", response);

        final String interval = "1M";
        List<Map<String, String>> documentCount = elasticSearchService.getDocumentCount(query, from, to, interval);
        final List<String> headers = Arrays.asList("yyyymm", "doccnt");
        httpResponseCsvWriter.setHeaders(headers);
        for (Map<String, String> entry : documentCount)
            httpResponseCsvWriter.write(entry.get("yyyymm") + "," + entry.get("doccnt"));
        httpResponseCsvWriter.close();
    }
}

// TODO: controlleradvice without requestparam
// TODO: swagger doc
