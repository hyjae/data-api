package kr.datasolution.ds.api.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NewsNamedEntitySummaryList {

    @SerializedName("entity_name")
    String entityName;

    @SerializedName("num_total_terms")
    Long numTotalTerms;

    @SerializedName("news_named_entity_summary_list")
    List<NewsNamedEntitySummary> newsNamedEntitySummaryList = new ArrayList<>();

    public void add(NewsNamedEntitySummary newsNamedEntitySummary) {
        newsNamedEntitySummaryList.add(newsNamedEntitySummary);
    }
}
