package kr.datastation.api.repository.datastation;

import kr.datastation.api.model.dataset.WeatherArea;
import kr.datastation.api.model.dataset.WeatherDaily;
import kr.datastation.api.model.dataset.WeatherDaily_;
import kr.datastation.api.model.datastation.Category;
import kr.datastation.api.model.datastation.Dataset;
import kr.datastation.api.util.CommonUtils;
import org.elasticsearch.common.collect.Tuple;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DatasetRepositoryImpl implements DatasetRepositoryCustom {

    @PersistenceContext(unitName = "dataStationEntityManagerFactory")
    private EntityManager entityManager;

//    @Override
//    public List<Tuple> findByColumnNameAndByWDateBetweenAndByAreaCode(
//            List<String> columnNames, String from, String to, Integer[] areaCode) {
//        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
//        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
//        List<Predicate> predicates = new ArrayList<>();
//
//        Root<WeatherDaily> weatherDaily = cq.from(WeatherDaily.class);
//        Join<WeatherDaily, WeatherArea> weatherArea = weatherDaily.join(WeatherDaily_.areaCode, JoinType.INNER);
//
//        final String dateFormat = "yyyyMMdd";
//        Date fromDate = CommonUtils.convertToDate(from, dateFormat);
//        Date toDate = CommonUtils.convertToDate(to, dateFormat);
//
//        Predicate fromDatePredicate = cb.greaterThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), fromDate);
//        Predicate toDatePredicate = cb.lessThanOrEqualTo(weatherDaily.get("wDate").as(Date.class), toDate);
//        Order orderByAreaCode = cb.asc(weatherDaily.get("areaCode"));
//        Order orderByWDate = cb.asc(weatherDaily.get("wDate"));
//
//        if (areaCode != null) {
//            List<Predicate> areaRangePredicateList = new ArrayList<>();
//            for (Integer code : areaCode) {
//                Integer fromRange = Integer.valueOf(code + "00");
//                Integer toRange = Integer.valueOf(code + "99");
//                Predicate areaCodeFromPredicate = cb.greaterThanOrEqualTo(weatherDaily.get("areaCode"), fromRange);
//                Predicate areaCodeToPredicate = cb.lessThanOrEqualTo(weatherDaily.get("areaCode"), toRange);
//                areaRangePredicateList.add(cb.and(areaCodeFromPredicate, areaCodeToPredicate));
//            }
//            predicates.add(cb.or(areaRangePredicateList.toArray(new Predicate[0])));
//        }
//        predicates.add(fromDatePredicate);
//        predicates.add(toDatePredicate);
//
//        List<Selection<?>> s = new LinkedList<>();
//        s.add(weatherDaily.get(WeatherDaily_.wDate));
//        s.add(weatherArea.get(WeatherArea_.areaCode));
//        s.add(weatherArea.get(WeatherArea_.mainName));
//        s.add(weatherArea.get(WeatherArea_.subName));
//        s.add(weatherArea.get(WeatherArea_.cityName));
//
//        for (String columnName : columnNames) { // TODO:
//            try {
//                s.add(weatherDaily.get(CaseUtils.toCamelCase(columnName, false, '_')));
//            } catch (IllegalArgumentException e) {
//                throw new ResourceNotFoundException(columnName, "dataset", columnName);
//            }
//        }
//        cq.multiselect(s).where(predicates.toArray(new Predicate[]{}));
//        cq.orderBy(orderByAreaCode, orderByWDate);
//        return entityManager.createQuery(cq).getResultList();
//    }

    @Override
    public List<DatasetCustomView> findAllByCategoryCtgrIdAndDsCodeList(String ctgrCode, List<String> dsCodeList) {

        // populate DTO class using JPA CriteriaQuery
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<DatasetCustomView> cq = cb.createQuery(DatasetCustomView.class);
        Root<Dataset> dataset = cq.from(Dataset.class);

        List<Predicate> predicates = new ArrayList<>();
        Predicate ctgrIdPredicate = cb.equal(dataset.get("category").get("ctgrCode"), ctgrCode);
        for (String dsCode : dsCodeList) {
            Predicate dsCodePredicate = cb.equal(dataset.get("dsCode"), dsCode);
            predicates.add(cb.or(cb.and(ctgrIdPredicate, dsCodePredicate)));
        }
        cq.select(cb.construct(DatasetCustomView.class, dataset.get("category"))).where(predicates.toArray(new Predicate[]{}));
        TypedQuery<DatasetCustomView> query = entityManager.createQuery(cq);
        List<DatasetCustomView> resultList = query.getResultList();
        return resultList;
    }
}
