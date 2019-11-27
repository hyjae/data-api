package kr.datasolution.ds.api.repository;


import kr.datasolution.ds.api.domain.WeatherDaily;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


@Repository
public class WeatherDailyRepositoryImpl implements WeatherDailyRepository {
    /**
     *  https://www.baeldung.com/spring-data-jpa-query
     *  https://stackoverflow.com/questions/41806152/add-criteriabuilder-betweendate-to-predicate
     */
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object> findByColumnName(List<String> columnNames, String from, String to) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<WeatherDaily> weatherDaily = query.from(WeatherDaily.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.between(weatherDaily.get("date"), from, to));

        List<Selection<?>> s = new LinkedList<>();
        s.add(columnNames.get(0).);
        columnNames.stream().map(i -> );

        query.multiselect(weatherDaily.get(columnName)).where(predicates.toArray(new Predicate[]{}));
        List<Object> collect = entityManager.createQuery(query)
                .getResultList()
                .stream()
                .map(i -> i.get(0))
                .collect(Collectors.toList());
        return collect;
    }
}
