package kr.datasolution.ds.api.repository;


import kr.datasolution.ds.api.domain.WeatherArea;
import kr.datasolution.ds.api.domain.WeatherDaily;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class WeatherDailyRepositoryImpl implements WeatherDailyRepositoryCustom  {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object> findByColumnName(List<String> columnNames, String from, String to) {
//        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
//        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
//
//        Metamodel m = entityManager.getMetamodel();
//        EntityType<WeatherDaily> WeatherDaily_ = m.entity(WeatherDaily.class);

//        Root<WeatherDaily> weatherDaily = cq.from(WeatherDaily.class);
//        Join<WeatherDaily, WeatherArea> joinTable = weatherDaily.join(WeatherDaily_.getSet("areaCode", WeatherArea.class));

//
//        List<Selection<?>> s = new LinkedList<>();
//        columnNames.stream().map(i -> s.add(root.get(i)));
//        s.add(joinTable.get("area_code"));
//
//        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(cb.between(root.get("date"), from, to));
//        cq.multiselect(s).where(predicates.toArray(new Predicate[]{}));
//
//        List<Object> collect = entityManager.createQuery(cq)
//                .getResultList()
//                .stream()
//                .map(i -> i.get(0))
//                .collect(Collectors.toList());
//        return collect;
        return null;
    }
}
