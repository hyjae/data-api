package kr.datastation.api.repository.dataset;


import kr.datastation.api.model.dataset.WeatherArea;
import kr.datastation.api.model.dataset.WeatherArea_;
import kr.datastation.api.model.dataset.WeatherDaily;
import kr.datastation.api.model.dataset.WeatherDaily_;
import kr.datastation.api.util.ReflectionUtils;
import kr.datastation.api.util.CommonUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.util.*;

@Repository
public class WeatherDailyRepositoryImpl implements WeatherDailyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tuple> findByColumnNameAndByWDateBetweenAndByAreaCode(
            List<String> columnNames, String from, String to, Integer[] areaCode) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<WeatherDaily> weatherDaily = cq.from(WeatherDaily.class);
        Join<WeatherDaily, WeatherArea> weatherArea = weatherDaily.join(WeatherDaily_.areaCode, JoinType.INNER);

        final String dateFormat = "yyyyMMdd";
        Date fromDate = CommonUtils.convertToDate(from, dateFormat);
        Date toDate = CommonUtils.convertToDate(to, dateFormat);

        Predicate fromDatePredicate = cb.greaterThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), fromDate);
        Predicate toDatePredicate = cb.lessThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), toDate);
        Order orderByAreaCode = cb.asc(weatherDaily.get("areaCode"));
        Order orderByWDate = cb.asc(weatherDaily.get("wDate"));

        if (areaCode != null) {
            List<Predicate> areaRangePredicateList = new ArrayList<>();
            for (Integer code : areaCode) {
                Integer fromRange = Integer.valueOf(code + "00");
                Integer toRange = Integer.valueOf(code + "99");
                Predicate areaCodeFromPredicate = cb.greaterThanOrEqualTo(weatherDaily.get("areaCode"), fromRange);
                Predicate areaCodeToPredicate = cb.lessThanOrEqualTo(weatherDaily.get("areaCode"), toRange);
                areaRangePredicateList.add(cb.and(areaCodeFromPredicate, areaCodeToPredicate));
            }
            predicates.add(cb.or(areaRangePredicateList.toArray(new Predicate[0])));
        }
        predicates.add(fromDatePredicate);
        predicates.add(toDatePredicate);

        List<Selection<?>> s = new LinkedList<>();
        s.add(weatherDaily.get(WeatherDaily_.wDate));
        s.add(weatherArea.get(WeatherArea_.areaCode));
        s.add(weatherArea.get(WeatherArea_.mainName));
        s.add(weatherArea.get(WeatherArea_.subName));
        s.add(weatherArea.get(WeatherArea_.cityName));

        for (String columnName : columnNames) { // TODO: exception
            s.add(weatherDaily.get(CaseUtils.toCamelCase(columnName, false, '_')));
        }
        cq.multiselect(s).where(predicates.toArray(new Predicate[]{}));
        cq.orderBy(orderByAreaCode, orderByWDate);
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Tuple> findByWDateBetweenAndByAreaCode(String from, String to, Integer[] areaCode) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<WeatherDaily> weatherDaily = cq.from(WeatherDaily.class);
        Join<WeatherDaily, WeatherArea> weatherArea = weatherDaily.join(WeatherDaily_.areaCode, JoinType.INNER);

        final String dateFormat = "yyyyMMdd";
        Date fromDate = CommonUtils.convertToDate(from, dateFormat);
        Date toDate = CommonUtils.convertToDate(to, dateFormat);

        Predicate fromDatePredicate = cb.greaterThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), fromDate);
        Predicate toDatePredicate = cb.lessThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), toDate);
        Order orderByAreaCode = cb.asc(weatherDaily.get("areaCode"));
        Order orderByWDate = cb.asc(weatherDaily.get("wDate"));

        if (areaCode != null) {
            List<Predicate> areaRangePredicateList = new ArrayList<>();
            for (Integer code : areaCode) {
                Integer fromRange = Integer.valueOf(code + "00");
                Integer toRange = Integer.valueOf(code + "99");
                Predicate areaCodeFromPredicate = cb.greaterThanOrEqualTo(weatherDaily.get("areaCode"), fromRange);
                Predicate areaCodeToPredicate = cb.lessThanOrEqualTo(weatherDaily.get("areaCode"), toRange);
                areaRangePredicateList.add(cb.and(areaCodeFromPredicate, areaCodeToPredicate));
            }
            predicates.add(cb.or(areaRangePredicateList.toArray(new Predicate[0])));
        }

        predicates.add(fromDatePredicate);
        predicates.add(toDatePredicate);

        List<Selection<?>> s = new LinkedList<>();

        s.add(weatherDaily.get(WeatherDaily_.wDate));
        s.add(weatherArea.get(WeatherArea_.areaCode));
        s.add(weatherArea.get(WeatherArea_.mainName));
        s.add(weatherArea.get(WeatherArea_.subName));
        s.add(weatherArea.get(WeatherArea_.cityName));

        EntityType<WeatherDaily> model = weatherDaily.getModel();

        List<String> valNames = ReflectionUtils.getValNames(WeatherDaily.class);

        List<String> ignoreColumnNames = new ArrayList<>(
                Arrays.asList("dailyId", "wDate", "areaCode", "insertDdtt", "updateDdtt"));
        for (String valName : valNames) {
            if (!ignoreColumnNames.contains(valName))
                s.add(weatherDaily.get(model.getSingularAttribute(valName)));
        }
        cq.multiselect(s).where(predicates.toArray(new Predicate[]{}));
        cq.orderBy(orderByAreaCode, orderByWDate);
        return entityManager.createQuery(cq).getResultList();
    }
}
