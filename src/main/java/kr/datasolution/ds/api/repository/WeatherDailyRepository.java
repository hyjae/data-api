package kr.datasolution.ds.api.repository;


import kr.datasolution.ds.api.domain.WeatherDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

@Repository
public interface WeatherDailyRepository extends JpaRepository<WeatherDaily, Long>, WeatherDailyRepositoryCustom {

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE), // TODO: test
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Query(value = "SELECT * FROM weather_daily WHERE w_date between ?1 and ?2", nativeQuery = true)
    Stream<WeatherDaily> findByWDateBetween(String fromDate, String toDate);
}
