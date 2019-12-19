package kr.datastation.api.repository.datastation;


import kr.datastation.api.model.datastation.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    // TODO: full text search
    List<DatasetCustomView> findByDsDescContainingOrDsKeywordContaining(String desc, String keyword);
}
