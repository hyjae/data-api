package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datasolution.ds.api.domain.*;
import kr.datasolution.ds.api.repository.WeatherDailyRepository;
import kr.datasolution.ds.api.util.HttpResponseCSVWriter;
import kr.datasolution.ds.api.util.ReflectionUtils;
import kr.datasolution.ds.api.validator.DateRequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static kr.datasolution.ds.api.util.CommonUtils.tupleToCSVFormat;

@RestController
@RequestMapping("/weather")
@Api("/weather")
public class WeatherController {

    @PersistenceContext
    EntityManager entityManager;

    final WeatherDailyRepository weatherDailyRepository;

    public WeatherController(WeatherDailyRepository weatherDailyRepository) {
        this.weatherDailyRepository = weatherDailyRepository;
    }

    @RequestMapping(value = "/{dataset}/download", method = RequestMethod.GET, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void getDataDownload(HttpServletResponse response,
                                @PathVariable String dataset,
                                @RequestParam(required = false, defaultValue = "csv") String format,
                                @DateRequestParam(point = TimePoint.FROM) String from,
                                @DateRequestParam(point = TimePoint.TO) String to,
                                @RequestParam(required = false) Integer[] area_code) throws IOException {

        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("weather.csv", response);

        String datasetName = dataset.replace("-", "_");
        List<String> columnNames = Arrays.asList("w_date", "area_code", "main_name", "sub_name", "city_name", datasetName);
        httpResponseCsvWriter.setHeaders(columnNames);

        // TODO: area_code validity check
        ArrayList<String> datasets = new ArrayList<>(Collections.singletonList(datasetName));
        List<Tuple> resultList = weatherDailyRepository.findByColumnNameAndByWDateBetweenAndByAreaCode(datasets, from, to, area_code);
        resultList.forEach(
                element -> httpResponseCsvWriter.write(tupleToCSVFormat(element))
        );
        httpResponseCsvWriter.close();
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @Transactional(readOnly = true) // TODO: ?
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void downloadFullCSV(HttpServletResponse response,
                                @RequestParam(required = false, defaultValue = "csv") String format,
                                @DateRequestParam(point = TimePoint.FROM) String from,
                                @DateRequestParam(point = TimePoint.TO) String to,
                                @RequestParam(required = false) Integer[] areaCode) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("weather.csv", response);

        // TODO: bug @Column
        List<String> columnNames = ReflectionUtils.getColumnNames(WeatherDaily.class);
        columnNames.remove(0); // delete idx
        columnNames.add(1, "area_code");
        columnNames.add(2, "main_name");
        columnNames.add(3, "sub_name");
        columnNames.add(4, "city_name");
        httpResponseCsvWriter.setHeaders(columnNames);

        // TODO: area_code validity check
        List<Tuple> weatherDailyStream = weatherDailyRepository.findByWDateBetweenAndByAreaCode(from, to, areaCode);
        weatherDailyStream.forEach(
                element -> httpResponseCsvWriter.write(tupleToCSVFormat(element))
        );
    }

    @RequestMapping(value = "/meta", method = RequestMethod.GET)
    public void getMetaData(@RequestParam("dataType") String dataType) {
        // TODO:
    }
}
