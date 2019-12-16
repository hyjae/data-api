package kr.datasolution.ds.api.repository;


import kr.datasolution.ds.api.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long>, CalendarRepositoryCustom {
    // TODO: full text search
    @Query(value = "SELECT ds_category, ds_code, ds_name, ds_desc, ds_keyword, ds_ver_major, ds_format, ds_source, ds_period, ds_start_ddtt, ds_end_ddtt, ds_size, update_ddtt FROM datastation_a.dataset_info WHERE dataset_info.ds_desc LIKE %:query% OR dataset_info.ds_keyword LIKE %:query%", nativeQuery = true)
    List<DatasetCustomView> findBySolYmdBetweenStream(@Param("query") String query);
}
