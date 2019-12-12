package kr.datasolution.ds.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class NewsRelatedTopicList {
    List<NewsRelatedTopic> newsRelatedTopicList = new ArrayList<>();

    public void add(NewsRelatedTopic newsRelatedTopic) {
        this.newsRelatedTopicList.add(newsRelatedTopic);
    }
}
