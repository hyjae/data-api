package kr.datastation.api.repository.datastation;

import kr.datastation.api.model.datastation.Dataset;
import org.springframework.context.ApplicationContext;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DatasetRepositoryImpl implements DatasetRepositoryCustom {

    @PersistenceContext(unitName = "dataStationEntityManagerFactory")
    private EntityManager entityManager;

    private final ApplicationContext context;

    public DatasetRepositoryImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public List<DatasetCustomView> findAllByCategoryCtgrIdAndDsCodeList(String ctgrCode, List<String> dsCodeList) {

        // populate DTO class using JPA CriteriaQuery
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Dataset> cq = cb.createQuery(Dataset.class);
        Root<Dataset> dataset = cq.from(Dataset.class);

        List<Predicate> predicateList = new ArrayList<>();
        Predicate ctgrIdPredicate = cb.equal(dataset.get("category").get("ctgrCode"), ctgrCode);
        if (dsCodeList == null) {
            predicateList.add(ctgrIdPredicate);
        } else {
            for (String dsCode : dsCodeList) {
                Predicate dsCodePredicate = cb.equal(dataset.get("dsCode"), dsCode);
                predicateList.add(cb.and(ctgrIdPredicate, dsCodePredicate));
            }
        }
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.or(predicateList.toArray(new Predicate[0])));

        cq.select(dataset).where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Dataset> query = entityManager.createQuery(cq);
        List<Dataset> dtoResultList = query.getResultList(); // does not optimize

        // DTO to interface
        SpelAwareProxyProjectionFactory pf = new SpelAwareProxyProjectionFactory();
        pf.setBeanFactory(context);

        List<DatasetCustomView> resultList = dtoResultList.stream()
                .map(c -> pf.createProjection(DatasetCustomView.class, c))
                .collect(Collectors.toList());
        return resultList;
    }
}
