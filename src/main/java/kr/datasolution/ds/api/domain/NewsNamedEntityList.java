package kr.datasolution.ds.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NewsNamedEntityList {
    Long totalHits;
    List<NewsNamedEntity> newsNamedEntityList = new ArrayList<>();

    public void add(NewsNamedEntity newsNamedEntity) {
        newsNamedEntityList.add(newsNamedEntity);
    }
}
