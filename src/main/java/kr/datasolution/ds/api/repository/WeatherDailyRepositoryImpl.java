package kr.datasolution.ds.api.repository;


import kr.datasolution.ds.api.domain.WeatherArea;
import kr.datasolution.ds.api.domain.WeatherArea_;
import kr.datasolution.ds.api.domain.WeatherDaily;
import kr.datasolution.ds.api.domain.WeatherDaily_;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static kr.datasolution.ds.api.util.CommonUtils.convertToDate;

@Repository
public class WeatherDailyRepositoryImpl implements WeatherDailyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tuple> findByColumnName(List<String> columnNames, String from, String to) throws IllegalArgumentException {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<WeatherDaily> weatherDaily = cq.from(WeatherDaily.class);
        Join<WeatherDaily, WeatherArea> weatherArea = weatherDaily.join(WeatherDaily_.areaCode, JoinType.INNER);

        final String dateFormat = "yyyyMMdd";
        Date fromDate = convertToDate(from, dateFormat);
        Date toDate = convertToDate(to, dateFormat);

        Predicate fromDatePredicate = cb.greaterThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), fromDate);
        Predicate toDatePredicate = cb.lessThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), toDate);

        predicates.add(fromDatePredicate);
        predicates.add(toDatePredicate);

        List<Selection<?>> s = new LinkedList<>();

        s.add(weatherDaily.get(WeatherDaily_.wDate));
        s.add(weatherArea.get(WeatherArea_.areaCode));
        s.add(weatherArea.get(WeatherArea_.mainName));
        s.add(weatherArea.get(WeatherArea_.subName));
        s.add(weatherArea.get(WeatherArea_.cityName));

        for (String columnName : columnNames)
            s.add(weatherDaily.get(columnName));
        cq.multiselect(s).where(predicates.toArray(new Predicate[]{}));

        List<Tuple> resultList = entityManager.createQuery(cq).getResultList();
        return resultList;
    }
}
