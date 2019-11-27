package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.domain.WeatherDaily;
import kr.datasolution.ds.api.repository.WeatherDailyRepository;
import kr.datasolution.ds.api.util.WriteDataToCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
    public Long getDataDownload(@PathVariable("dataType") String dataType, String from, String to) {

        List<Object> byColumnName = weatherDailyRepository.findByColumnName(dataType, from, to);

        WriteDataToCSV.writeDataToCsvWithListObjects();
        ResponseEntity responseEntity = new ResponseEntity();

        return elasticSearchService.getDocMeanFrequency(query, startDate, endDate, dateHistogramInterval);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadFullCSV(HttpServletResponse response, String from, String to) {
        response.addHeader("Content-Type", "application/csv");
        response.addHeader("Content-Disposition", "attachment; filename=weather.csv");
        response.setCharacterEncoding("UTF-8");
        try (Stream<WeatherDaily> weatherDailyStream = weatherDailyRepository.getAll();) {
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
