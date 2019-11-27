package kr.datasolution.ds.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@Table(name = "weather_daily")
public class WeatherDaily {

    @Id
    @Column(name = "daily_id")
    private Integer dailyId;

    @Column(name = "w_date")
    private Date wDate;

    @Column(name = "area_code")
    private Integer areaCode;

    @Column(name = "actual_Yn")
    private Integer actualYn;

    @Column(name = "w_description")
    private Integer WDescription;

    @Column(name = "w_avg_ta")
    private Float wAvgTa;

    @Column(name = "w_max_ta")
    private Float wMaxTa;

    @Column(name = "w_min_ta")
    private Float wMinTa;

    @Column(name = "w_prob_rn")
    private Float wProbRn;

    @Column(name = "w_sum_rn")
    private Float wSumRn;

    @Column(name = "w_dd_mefs")
    private Float wDdMefs;

    @Column(name = "w_max_wd")
    private Integer wMaxWd;

    @Column(name = "w_avg_ws")
    private Float wAvgWs;

    @Column(name = "w_avg_rhm")
    private Float wAvgRhm;

    @Column(name = "w_avg_tca")
    private Float wAvgTca;

    @Column(name = "a_pm10")
    private Integer aPm10;

    @Column(name = "a_pm25")
    private Integer aPm25;

    @Column(name = "a_o3")
    private Integer aO3;

    @Column(name = "s_01_swind")
    private Integer s01Swind;

    @Column(name = "s_02_hrain")
    private Integer s02Hrain;

    public String toString() {
        return String.join(",", "" + this.getDailyId(), "" + this.getWDate(), "" + this.getAreaCode());
    }
}
