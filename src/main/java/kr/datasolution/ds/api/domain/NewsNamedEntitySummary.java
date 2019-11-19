package kr.datasolution.ds.api.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NewsNamedEntitySummary {

    @SerializedName("key_word")
    @NonNull private String keyWord;

    @SerializedName("doc_count")
    @NonNull private Long docCount;
}
