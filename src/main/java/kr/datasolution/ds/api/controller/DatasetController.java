package kr.datasolution.ds.api.controller;

import io.swagger.annotations.Api;
import kr.datasolution.ds.api.repository.DatasetCustomView;
import kr.datasolution.ds.api.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dataset")
@Api("/dataset")
public class DatasetController {

    final DatasetRepository datasetRepository;

    @Autowired
    DatasetController(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    @RequestMapping(value = "/search",  method = RequestMethod.GET)
    public List<DatasetCustomView> searchWeatherDataset(String query) {
        List<DatasetCustomView> bySolYmdBetweenStream = datasetRepository.findBySolYmdBetweenStream(query);
        return datasetRepository.findBySolYmdBetweenStream(query);
    }
}
