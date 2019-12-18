package kr.datastation.api.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NewsNamedEntityCSVObject {
    @NonNull String keyWord;
    @NonNull String namedEntity;
    @NonNull Long docCount;

    public String toCSV() {
        return keyWord + ", " + namedEntity + ", " + docCount.toString();
    }
}
