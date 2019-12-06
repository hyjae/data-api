package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datasolution.ds.api.domain.*;
import kr.datasolution.ds.api.repository.WeatherDailyRepository;
import kr.datasolution.ds.api.util.CommonUtils;
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "from", value = "String", dataType = "String", paramType = "query", example = "20180101"),
            @ApiImplicitParam(name = "to", value = "String", dataType = "String", paramType = "query", example = "20180211"),
    })
    public void getDataDownload(HttpServletResponse response,
                                @PathVariable String dataset,
                                @RequestParam(required = false, defaultValue = "csv") String format,
                                @DateRequestParam(point = TimePoint.FROM) String from,
                                @DateRequestParam(point = TimePoint.TO) String to) throws IOException {
        // TODO: global exception
        // TODO: json
        // TODO: func
        String datasetName = dataset.replace("-", "_");
        List<String> colNames = Arrays.asList("w_date", "area_code", "main_name", "sub_name", "city_name", datasetName);

        if (format.equalsIgnoreCase("csv")) {
            ArrayList<String> datasets = new ArrayList<>(Collections.singletonList(datasetName));
            List<Tuple> resultList = weatherDailyRepository.findByColumnName(datasets, from, to);

            response.addHeader("Content-Type", "application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=weather.csv");
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();
            out.write(CommonUtils.listToCSVFormat(colNames));
            out.write("\n");
            resultList.forEach(
                    tupleData -> {
                        out.write(tupleToCSVFormat(tupleData));
                        out.write("\n");
//                        entityManager.detach(tupleData); // TODO: ?
                    });
            out.flush();
            out.close();
        }
    }

    @RequestMapping(value = "/meta", method = RequestMethod.GET)
    public void getMetaData(@RequestParam("dataType") String dataType) {
        // TODO:
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
                                @DateRequestParam(point = TimePoint.TO) String to) throws IOException {
        if (format.equalsIgnoreCase("csv")) {
            response.addHeader("Content-Type", "application/csv");
            response.addHeader("Content-Disposition", "attachment; filename=weather.csv");
            response.setCharacterEncoding("UTF-8");

            Stream<WeatherDaily> weatherDailyStream = weatherDailyRepository.getAllBetween(from, to);

            List<String> tableColumnNames = ReflectionUtils.getColumnNames(WeatherDaily.class); // TODO: bug @Column
            tableColumnNames.remove(0); // delete idx
            tableColumnNames.add(1, "area_code");
            tableColumnNames.add(2, "main_name");
            tableColumnNames.add(3, "sub_name");
            tableColumnNames.add(4, "city_name");

            PrintWriter out = response.getWriter();
            out.write(CommonUtils.listToCSVFormat(tableColumnNames));
            out.write("\n");
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
}
