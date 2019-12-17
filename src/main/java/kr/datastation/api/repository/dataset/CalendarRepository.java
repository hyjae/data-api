package kr.datastation.api.repository.dataset;

import kr.datastation.api.model.dataset.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @QueryHints(value = {
            @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MIN_VALUE),
            @QueryHint(name = HINT_CACHEABLE, value = "false"),
            @QueryHint(name = READ_ONLY, value = "true")
    })
    @Query(value = "SELECT * FROM calendar WHERE sol_ymd between ?1 and ?2", nativeQuery = true)
    Stream<Calendar> findBySolYmdBetweenStream(String from, String to);

    <T> List<T> findBySolYmdBetweenOrderBySolYmd(String from, String to, Class<T> type);

    List<CalendarCustomView> findBySolYmdBetweenOrderBySolYmd(String from, String to);
}
