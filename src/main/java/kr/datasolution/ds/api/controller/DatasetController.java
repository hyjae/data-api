package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.model.WeatherDaily;
import kr.datasolution.ds.api.repository.DatasetCustomView;
import kr.datasolution.ds.api.repository.DatasetRepository;
import kr.datasolution.ds.api.repository.WeatherDailyRepository;
import kr.datasolution.ds.api.util.HttpResponseCSVWriter;
import kr.datasolution.ds.api.util.ReflectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


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

    @RequestMapping(value = "/search",  method = RequestMethod.GET)
    public List<DatasetCustomView> searchWeatherDataset(String query) {
        return datasetRepository.findBySolYmdBetweenStream(query);
    }

    @RequestMapping(value = "/download/latest", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public void downloadLatestData(HttpServletResponse response,
                                @RequestParam(required = false, defaultValue = "csv") String format) throws IOException {
        HttpResponseCSVWriter httpResponseCsvWriter = new HttpResponseCSVWriter("dataset.csv", response);

        // TODO: bug @Column
        List<String> columnNames = ReflectionUtils.getColumnNames(WeatherDaily.class);
        columnNames.remove(0); // delete idx
        columnNames.add(1, "area_code");
        columnNames.add(2, "main_name");
        columnNames.add(3, "sub_name");
        columnNames.add(4, "city_name");
        columnNames.remove(columnNames.size()-1);
        columnNames.remove(columnNames.size()-1);
        httpResponseCsvWriter.setHeaders(columnNames);

        DateTime today = DateTime.now();
        DateTime from = today.minusYears(1);

        List<WeatherDaily> weatherDailyList = weatherDailyRepository.findByWDateBetween(from.toString(), today.toString());


//        List<WeatherDaily> weatherDailyList = weatherDailyRepository.findByWDateBetween(from.toString(), today.toString());
//        weatherDailyList.forEach(
//                element -> httpResponseCsvWriter.write(element.toString())
//        );
    }
}
