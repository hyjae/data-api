package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import kr.datastation.api.model.datastation.Dataset;
import kr.datastation.api.repository.datastation.DatasetCustomView;
import kr.datastation.api.repository.datastation.DatasetRepository;
import kr.datastation.api.repository.dataset.WeatherDailyRepository;
import kr.datastation.api.util.HttpResponseCSVWriter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import static kr.datastation.api.util.CommonUtils.tupleToCSVFormat;


@RestController
@RequestMapping("/dataset")
@Api("/dataset")
public class DatasetController {

    final DatasetRepository datasetRepository;
    final WeatherDailyRepository weatherDailyRepository;

    @Autowired
    DatasetController(DatasetRepository datasetRepository, WeatherDailyRepository weatherDailyRepository) {
        this.datasetRepository = datasetRepository;
        this.weatherDailyRepository = weatherDailyRepository;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public void searchWeatherDataset(HttpServletResponse response, String query) {
        List<DatasetCustomView> byDsDescContainingOrDsKeyword = datasetRepository.findByDsDescContainingOrDsKeywordContaining(query, query);
        List<String> collect = byDsDescContainingOrDsKeyword.stream().map(i -> i.getDsCode()).collect(Collectors.toList());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        DateTime now = DateTime.now();
        String format = simpleDateFormat.format(now.toDate());
        String format1 = simpleDateFormat.format(now.minusYears(1).toDate());
        List<Tuple> t = weatherDailyRepository.findByColumnNameAndByWDateBetweenAndByAreaCode(collect, format, format1, null);
        HttpResponseCSVWriter httpResponseCSVWriter = new HttpResponseCSVWriter("dataset.csv", response);
        t.forEach(
                element -> httpResponseCSVWriter.write(tupleToCSVFormat(element))
        );
    }

//    @RequestMapping(value = "/download/latest", method = RequestMethod.GET, produces = "application/json")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
//            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
//    })
//    public void downloadLatestData(HttpServletResponse response, @PathVariable String dataset,
//                                @RequestParam(required = false, defaultValue = "csv") String format,
//                                @DateRequestParam(point = TimePoint.FROM) String from, @DateRequestParam(point = TimePoint.TO) String to, // TODO: areacode validity check
//                                @RequestParam(value = "areacode", required = false) Integer[] areaCode) throws IOException, IllegalArgumentException {
//        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("weather.csv", response);
//
//        String datasetName = dataset.replace("-", "_");
//        List<String> columnNames = Arrays.asList("w_date", "area_code", "main_name", "sub_name", "city_name", datasetName);
//        httpResponseCsvWriter.setHeaders(columnNames);
//
//        ArrayList<String> datasets = new ArrayList<>(Collections.singletonList(datasetName));
//        List<Tuple> resultList = weatherDailyRepository.findByColumnNameAndByWDateBetweenAndByAreaCode(datasets, from, to, areaCode);
//        resultList.forEach(
//                element -> httpResponseCsvWriter.write(tupleToCSVFormat(element))
//        );
//        httpResponseCsvWriter.close();
//    }
//
//    @RequestMapping(value = "/download/latest", method = RequestMethod.GET)
//    @Transactional(readOnly = true)
//    public void downloadLatestData(HttpServletResponse response,
//                                @RequestParam(required = false, defaultValue = "csv") String format) throws IOException {
//        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("dataset.csv", response);
//
//        // TODO: bug @Column
//        List<String> columnNames = ReflectionUtils.getColumnNames(WeatherDaily.class);
//        columnNames.remove(0); // delete idx
//        columnNames.add(1, "area_code");
//        columnNames.add(2, "main_name");
//        columnNames.add(3, "sub_name");
//        columnNames.add(4, "city_name");
//        columnNames.remove(columnNames.size()-1);
//        columnNames.remove(columnNames.size()-1);
//        httpResponseCsvWriter.setHeaders(columnNames);
//
//        DateTime today = DateTime.now();
//        DateTime from = today.minusYears(1);
//
//        List<WeatherDaily> weatherDailyList = weatherDailyRepository.findByWDateBetween(from.toString(), today.toString());
//
//
////        List<WeatherDaily> weatherDailyList = weatherDailyRepository.findByWDateBetween(from.toString(), today.toString());
////        weatherDailyList.forEach(
////                element -> httpResponseCsvWriter.write(element.toString())
////        );
//    }
//}
}