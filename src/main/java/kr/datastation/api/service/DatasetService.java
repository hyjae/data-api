package kr.datastation.api.service;

import kr.datastation.api.repository.datastation.DatasetCustomView;
import kr.datastation.api.vo.SearchDatasetResult;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatasetService {

    public SearchDatasetResult generateSearchDatasetResult(List<DatasetCustomView> datasetList) {
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
