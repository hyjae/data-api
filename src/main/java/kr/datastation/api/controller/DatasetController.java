package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import kr.datastation.api.repository.datastation.DatasetCustomView;
import kr.datastation.api.repository.datastation.DatasetRepository;
import kr.datastation.api.repository.dataset.WeatherDailyRepository;
import kr.datastation.api.vo.SearchDatasetResult;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/dataset")
@Api("/dataset")
@Transactional // TODO: ?
public class DatasetController {

    final DatasetRepository datasetRepository;
    final WeatherDailyRepository weatherDailyRepository;

    @Autowired
    DatasetController(DatasetRepository datasetRepository, WeatherDailyRepository weatherDailyRepository) {
        this.datasetRepository = datasetRepository;
        this.weatherDailyRepository = weatherDailyRepository;
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public List<DatasetCustomView> getLatestDataset() {
        return datasetRepository.findTop10ByOrderByUpdateDdttDesc();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public SearchDatasetResult searchDataset(@RequestParam String query) {
        List<DatasetCustomView> datasetList = datasetRepository.findByDsDescContainingOrDsKeywordContaining(query, query);

        // TODO: generate download links for all dataset
//        StringBuilder downloadAllURL = new StringBuilder();
//        for (DatasetCustomView datasetCodeView : datasetList) {
//            String dsCode = datasetCodeView.getDsCode();
//            Set<CategoryDatasetMap> categoryDatasetMaps = datasetCodeView.getCategoryDatasetMaps();
//            for (CategoryDatasetMap categoryDatasetMap : categoryDatasetMaps) {
//                String ctgrCode = categoryDatasetMap.getCategory().getCtgrCode();
//                datasetURLList.put(ctgrCode)
//            }
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        DateTime now = DateTime.now();

        String todayDate = simpleDateFormat.format(now.toDate());
        String lastYearDate = simpleDateFormat.format(now.minusYears(1).toDate());

        StringBuilder downloadAllWeatherDatasetURL = new StringBuilder()
                .append("/weather/download/multiple?")
                .append("from=")
                .append(lastYearDate)
                .append("&to=")
                .append(todayDate).append("&dataset=");
        datasetList.forEach(i -> downloadAllWeatherDatasetURL.append(i.getDsCode()).append(","));
        int length = downloadAllWeatherDatasetURL.length();
        String downloadURL = downloadAllWeatherDatasetURL.delete(length-1, length).toString();

        SearchDatasetResult searchDatasetResult = new SearchDatasetResult();
        searchDatasetResult.setDownloadAllURL(downloadURL);
        searchDatasetResult.setDatasetCustomViewList(datasetList);

        return searchDatasetResult;
    }
}