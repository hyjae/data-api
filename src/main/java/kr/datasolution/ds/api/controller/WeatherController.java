package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.domain.WeatherDaily;
import kr.datasolution.ds.api.repository.WeatherDailyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static kr.datasolution.ds.api.util.CommonUtils.tupleToCSVFormat;

@RestController
@RequestMapping("/weather")
@Api("/weather")
public class WeatherController {

    @Autowired
    WeatherDailyRepository weatherDailyRepository;

    @PersistenceContext
    EntityManager entityManager;

    @RequestMapping(value = "/{dataset}/download", method = RequestMethod.GET, produces = "application/json")
    public void getDataDownload(HttpServletResponse response,
                                @PathVariable String dataset,
                                @RequestParam(value = "format") String format,
                                @RequestParam(value = "from", required = false) String from,
                                @RequestParam(value = "to", required = false) String to) throws IOException { // TODO: global exception
        // TODO: json
        // TODO: header
        // TODO: func
        if (format.equalsIgnoreCase("csv")) {
            ArrayList<String> datasets = new ArrayList<>(Collections.singletonList(dataset));
            List<Tuple> resultList = weatherDailyRepository.findByColumnName(datasets, from, to);

            response.addHeader("Content-Type", "application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=weather.csv");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();

            resultList.forEach(
                    tupleData -> {
                        out.write(tupleToCSVFormat(tupleData));
                        out.write("\n");
                    });
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "/meta", method = RequestMethod.GET)
    public void getMetaData(@RequestParam("dataType") String dataType) {
        // TODO:
    }

    @RequestMapping(value = "/download/full", method = RequestMethod.GET)
    @Transactional(readOnly = true) // TODO: ?
    public void downloadFullCSV(HttpServletResponse response,
                                @RequestParam(value = "format", required = false) String format,
                                @RequestParam(value = "from", required = false) String from, // TODO : exception
                                @RequestParam(value = "to", required = false) String to) throws IOException {
        // TODO: header
        response.addHeader("Content-Type", "application/csv");
        response.addHeader("Content-Disposition", "attachment; filename=weather.csv");
        response.setCharacterEncoding("UTF-8");

        Stream<WeatherDaily> weatherDailyStream = weatherDailyRepository.getAllBetween(from, to);

        PrintWriter out = response.getWriter();
        weatherDailyStream.forEach(
                weatherDaily -> {
                    out.write(weatherDaily.toString());
                    out.write("\n");
                    entityManager.detach(weatherDaily);
                });
        out.flush();
        out.close();
    }
}
