package kr.datastation.api.repository.dataset;


import kr.datastation.api.model.dataset.WeatherDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherDailyRepository extends JpaRepository<WeatherDaily, Long>, WeatherDailyRepositoryCustom {

//    @QueryHints(value = {
//            @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE), // TODO: test
//            @QueryHint(name = HINT_CACHEABLE, value = "false"),
//            @QueryHint(name = READ_ONLY, value = "true")
//    })
//    @Query(value = "SELECT * FROM weather_daily WHERE w_date between ?1 and ?2", nativeQuery = true)
//    Stream<WeatherDaily> findByWDateBetween(String fromDate, String toDate); // TODO: stream?
    @Query(value = "SELECT * FROM weather_daily WHERE w_date between ?1 and ?2", nativeQuery = true)
    List<WeatherDaily> findByWDateBetween(String fromDate, String toDate);
}
