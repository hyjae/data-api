package kr.datastation.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import kr.datastation.api.repository.datastation.DatasetCustomView;
import kr.datastation.api.repository.datastation.DatasetRepository;
import kr.datastation.api.repository.dataset.WeatherDailyRepository;
import kr.datastation.api.service.DatasetService;
import kr.datastation.api.vo.SearchDatasetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/dataset")
@Api("/dataset")
public class DatasetController {

    final DatasetRepository datasetRepository;
    final WeatherDailyRepository weatherDailyRepository;
    final DatasetService datasetService;

    @Autowired
    DatasetController(DatasetRepository datasetRepository, WeatherDailyRepository weatherDailyRepository, DatasetService datasetService) {
        this.datasetRepository = datasetRepository;
        this.weatherDailyRepository = weatherDailyRepository;
        this.datasetService = datasetService;
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    public List<DatasetCustomView> getLatestDataset() {
        return datasetRepository.findTop10ByOrderByUpdateDdttDesc();
    }

    @RequestMapping(value = "/popular", method = RequestMethod.GET)
    public List<DatasetCustomView> getPopularDataset() {
        return datasetRepository.findTop10ByOrderByDownloadCountDesc();
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.GET, params = "ctgrcode")
    public SearchDatasetResult retrieveDatasetByCode(@RequestParam(value = "ctgrcode") String ctgrCode,
                                                     @RequestParam(value = "dscode", required = false) List<String> dsCodeList) {
        List<DatasetCustomView> datasetList = datasetRepository.findAllByCategoryCtgrIdAndDsCodeList(ctgrCode, dsCodeList);
        return datasetService.generateSearchDatasetResult(datasetList);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, params = "query")
    public SearchDatasetResult searchDataset(@RequestParam String query) {
        List<DatasetCustomView> datasetList = datasetRepository.findByDsDescContainingOrDsKeywordContaining(query, query);
        return datasetService.generateSearchDatasetResult(datasetList);
    }
}