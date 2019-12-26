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

    public String getWrittenTime() {
        try {
            return writtenTime.substring(0, 8);
        } catch(IndexOutOfBoundsException e) {}
        return writtenTime;
    }

    public String toCSV() {
        String titleWithNoComma = this.title.replace(",", " ");
        String writtenTime = "";
        try {
            writtenTime = this.writtenTime.substring(0, 8);
        } catch(IndexOutOfBoundsException e) {}
        return titleWithNoComma + ", " + String.join(" ", this.namedEntityList) + ", " + writtenTime;
    }
}
