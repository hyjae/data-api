package kr.datastation.api.validator;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interval {
    @ValidInterval
    private String interval;
}
