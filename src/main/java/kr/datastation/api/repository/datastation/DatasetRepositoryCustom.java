package kr.datastation.api.repository.datastation;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepositoryCustom {
    List<DatasetCustomView> findAllByCategoryCtgrIdAndDsCodeList(String ctgrCode, List<String> dsCodeList);
}
