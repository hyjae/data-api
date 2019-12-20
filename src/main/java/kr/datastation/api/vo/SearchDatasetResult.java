package kr.datastation.api.vo;

import kr.datastation.api.repository.datastation.DatasetCustomView;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SearchDatasetResult {
    String downloadAllURL;
    List<DatasetCustomView> datasetCustomViewList;
}
