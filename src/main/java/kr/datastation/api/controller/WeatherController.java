package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datastation.api.model.dataset.WeatherDaily;
import kr.datastation.api.repository.dataset.WeatherDailyRepository;
import kr.datastation.api.util.HttpResponseCSVWriter;
import kr.datastation.api.util.ReflectionUtils;
import kr.datastation.api.validator.DateRequestParam;
import kr.datastation.api.util.CommonUtils;
import kr.datastation.api.vo.TimePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/weather")
@Api("/weather")
public class WeatherController {

    final WeatherDailyRepository weatherDailyRepository;

    @Autowired
    public WeatherController(WeatherDailyRepository weatherDailyRepository) {
        this.weatherDailyRepository = weatherDailyRepository;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    @RequestMapping(value = "/download/multiple", method = RequestMethod.GET, produces = "application/json")
    public void downloadMultipleWeatherDataset(HttpServletResponse response,
                                       @RequestParam List<String> dataset,
                                       @RequestParam(required = false, defaultValue = "csv") String format,
                                       @DateRequestParam(point = TimePoint.FROM) String from,
                                       @DateRequestParam(point = TimePoint.TO) String to) throws IOException, IllegalArgumentException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("weather.csv", response);

        List<String> datasetList = dataset.stream().map(i -> i.replace("-", "_")).collect(Collectors.toList());
        List<String> columnNames = Arrays.asList("w_date", "area_code", "main_name", "sub_name", "city_name");
        columnNames.addAll(datasetList);
        httpResponseCsvWriter.setHeaders(columnNames);

        List<Tuple> resultList = weatherDailyRepository.findByColumnNameAndByWDateBetweenAndByAreaCode(columnNames, from, to, null);
        resultList.forEach(element -> httpResponseCsvWriter.write(CommonUtils.tupleToCSVFormat(element)));
        httpResponseCsvWriter.close();
    }

    @RequestMapping(value = "/{dataset}/download", method = RequestMethod.GET, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void downloadWeatherDataset(HttpServletResponse response,
                                       @PathVariable String dataset,
                                       @RequestParam(required = false, defaultValue = "csv") String format,
                                       @DateRequestParam(point = TimePoint.FROM) String from,
                                       @DateRequestParam(point = TimePoint.TO) String to,
                                       @RequestParam(value = "areacode", required = false) Integer[] areaCode) throws IOException, IllegalArgumentException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("weather.csv", response);

        String datasetName = dataset.replace("-", "_");
        List<String> columnNames = Arrays.asList("w_date", "area_code", "main_name", "sub_name", "city_name", datasetName);
        httpResponseCsvWriter.setHeaders(columnNames);

        ArrayList<String> datasetNameList = new ArrayList<>(Collections.singletonList(datasetName));
        List<Tuple> resultList = weatherDailyRepository.findByColumnNameAndByWDateBetweenAndByAreaCode(datasetNameList, from, to, areaCode);
        resultList.forEach(element -> httpResponseCsvWriter.write(CommonUtils.tupleToCSVFormat(element)));
        httpResponseCsvWriter.close();
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void downloadFullWeatherDataset(HttpServletResponse response,
                                           @RequestParam(required = false, defaultValue = "csv") String format,
                                           @DateRequestParam(point = TimePoint.FROM) String from,
                                           @DateRequestParam(point = TimePoint.TO) String to,
                                           @RequestParam(value = "areacode", required = false) Integer[] areaCode) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("weather.csv", response);

        List<String> columnNames = ReflectionUtils.getColumnNames(WeatherDaily.class);
        columnNames.remove(0); // delete idx
        columnNames.add(1, "area_code");
        columnNames.add(2, "main_name");
        columnNames.add(3, "sub_name");
        columnNames.add(4, "city_name");
        columnNames.remove(columnNames.size()-1);
        columnNames.remove(columnNames.size()-1);
        httpResponseCsvWriter.setHeaders(columnNames);

        List<Tuple> weatherDailyStream = weatherDailyRepository.findByWDateBetweenAndByAreaCode(from, to, areaCode);
        weatherDailyStream.forEach(element -> httpResponseCsvWriter.write(CommonUtils.tupleToCSVFormat(element)));
    }
}
