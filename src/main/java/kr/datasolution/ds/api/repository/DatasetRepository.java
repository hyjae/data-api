package kr.datasolution.ds.api.repository;


import kr.datasolution.ds.api.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long>, CalendarRepositoryCustom {
    // TODO: full text search
    List<DatasetCustomView> findByDsDescContainingOrDsKeyword(String desc, String keyword);
}
