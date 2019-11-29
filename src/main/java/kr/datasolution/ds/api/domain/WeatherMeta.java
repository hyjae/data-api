package kr.datasolution.ds.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.collect.Tuple;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class WeatherMeta {
    String source;
    String frequency;
    Tuple<Date, Date> duration;
    String type;
    String size;
    String version;
    String updated;
    List<String> keywords;
}
