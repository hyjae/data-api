package kr.datastation.api.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeLineChart implements Serializable {
    private String date;
    private double value;
    private String event;

    public String toCSV() {
        return this.date + ", " + this.event + ", "+ this.value;
    }
}
