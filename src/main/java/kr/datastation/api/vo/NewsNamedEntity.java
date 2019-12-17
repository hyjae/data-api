package kr.datastation.api.vo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class NewsNamedEntity {
    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("written_time")
    private String writtenTime;

    @SerializedName(value = "organizationNamedEntity", alternate = {"locationNamedEntity", "personNamedEntity", "etcNamedEntity", "totalNamedEntity"})
    private List<String> namedEntityList;
}
