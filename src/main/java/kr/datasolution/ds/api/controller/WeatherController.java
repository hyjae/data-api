package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.domain.WeatherDaily;
import kr.datasolution.ds.api.repository.WeatherDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/weather")
@Api("/weather")
public class WeatherController {

    @Autowired
    WeatherDailyRepository weatherDailyRepository;

    @PersistenceContext
    EntityManager entityManager;

    @RequestMapping(value = "/{dataType}/download", method = RequestMethod.GET, produces = "application/json")
    public Long getDataDownload(@PathVariable("dataType") String dataType,
                                @DateTimeFormat(pattern="yyyyMMdd") String from,
                                @DateTimeFormat(pattern="yyyyMMdd") String to) {

        ArrayList<String> columnNames = new ArrayList<>(Collections.singletonList(dataType));
        List<Object> byColumnName = weatherDailyRepository.findByColumnName(columnNames, from, to);

//        WriteDataToCSV.writeDataToCsvWithListObjects();
//        ResponseEntity responseEntity = new ResponseEntity();

//        return elasticSearchService.getDocMeanFrequency(query, startDate, endDate, dateHistogramInterval);
        return null;
    }
    @RequestMapping(value = "/{dataType}", method = RequestMethod.GET)
    public void getMetaData(@PathVariable("dataType") String dataType) {
        // TODO:
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @Transactional(readOnly = true) // TODO: ?
    public void downloadFullCSV(HttpServletResponse response,
                                @DateTimeFormat(pattern="yyyyMMdd") String from, // TODO : exception
                                @DateTimeFormat(pattern="yyyyMMdd") String to) {

        response.addHeader("Content-Type", "application/csv");
        response.addHeader("Content-Disposition", "attachment; filename=weather.csv");
        response.setCharacterEncoding("UTF-8");
        try (Stream<WeatherDaily> weatherDailyStream = weatherDailyRepository.getAllBetween(from, to)) {
            PrintWriter out = response.getWriter();
            weatherDailyStream.forEach(weatherDaily -> {
                out.write(weatherDaily.toString());
                out.write("\n");
                entityManager.detach(weatherDaily);
            });
            out.flush();
            out.close();
        } catch (IOException ix) {
            throw new RuntimeException("There is an error while downloading weather.csv", ix);
        }
    }
}
