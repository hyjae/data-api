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

    @RequestMapping(value = "/popular", method = RequestMethod.GET)
    public List<DatasetCustomView> getPopularDataset() {
        // TODO: popular
        return datasetRepository.findTop10ByOrderByDownloadCountDesc();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public SearchDatasetResult searchDataset(@RequestParam String query) {
        List<DatasetCustomView> datasetList = datasetRepository.findByDsDescContainingOrDsKeywordContaining(query, query);

        SearchDatasetResult searchDatasetResult = new SearchDatasetResult();
        if (datasetList.isEmpty())
            return searchDatasetResult;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        DateTime now = DateTime.now();

        String todayDate = simpleDateFormat.format(now.toDate());
        String lastYearDate = simpleDateFormat.format(now.minusYears(1).toDate());

        List<String> downloadAllURL = new ArrayList<>();
        String downloadURL = "%s/%s/download?format=csv&from=%s&to=%s";
        for (DatasetCustomView datasetCodeView : datasetList) {
            String categoryCode = datasetCodeView.getCategoryCode();
            String dsCode = datasetCodeView.getDsCode();
            downloadAllURL.add(String.format(downloadURL, categoryCode, dsCode, lastYearDate, todayDate));
        }
        searchDatasetResult.setDownloadURLList(downloadAllURL);
        searchDatasetResult.setDatasetCustomViewList(datasetList);
        return searchDatasetResult;
    }
}