package kr.datasolution.ds.api.domain;

import kr.datasolution.ds.api.util.BigDecimalConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "weather_daily")
public class WeatherDaily implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_id")
    private Integer dailyId;

    @Column(name = "w_date")
    private Date wDate;

    @ManyToOne(fetch = FetchType.LAZY) // TODO:
    @JoinColumn(name = "area_code")
    private WeatherArea areaCode;

    @Column(name = "actual_yn")
    private Integer actualYn;

    @Column(name = "w_description")
    private String wDescription;

    @Column(name = "w_avg_ta")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wAvgTa;

    @Column(name = "w_max_ta")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wMaxTa;

    @Column(name = "w_min_ta")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wMinTa;

    @Column(name = "w_prob_rn")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wProbRn;

    @Column(name = "w_sum_rn")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wSumRn;

    @Column(name = "w_dd_mefs")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wDdMefs;

    @Column(name = "w_max_wd")
    private Integer wMaxWd;

    @Column(name = "w_avg_ws")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wAvgWs;

    @Column(name = "w_avg_rhm")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wAvgRhm;

    @Column(name = "w_avg_tca")
//    @Convert(converter = BigDecimalConverter.class)
    private BigDecimal wAvgTca;

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

    @Column(name = "s_03_cold")
    private Integer s03Cold;

    @Column(name = "s_04_dry")
    private Integer s04Dry;

    @Column(name = "s_05_ssurge")
    private Integer s05Ssurge;

    @Column(name = "s_06_hsea")
    private Integer s06Hsea;

    @Column(name = "s_07_typoon")
    private Integer s07Typoon;

    @Column(name = "s_08_hsnow")
    private Integer s08Hsnow;

    @Column(name = "s_09_ydust")
    private Integer s09YDust;

    @Column(name = "s_10_resv")
    private Integer s10Resv;

    @Column(name = "s_11_resv")
    private Integer s11Resv;

    @Column(name = "s_12_hheat")
    private Integer s12Hheat;

    @Column(name = "insert_ddtt")
    private Timestamp insertDdtt; // Note: MySQL DATETIME maps to Timestamp, not DateTime!

    @Column(name = "update_ddtt")
    private Timestamp updateDdtt;

    public String toString() {
        StringBuilder text = new StringBuilder();
        Field[] declaredFields = WeatherDaily.class.getDeclaredFields();

        for (int i = 1; i < declaredFields.length; ++i) { // skip index
            try {
                Object o = declaredFields[i].get(this);
                if (o == null) {
                    text.append("NULL");
                } else if (o instanceof WeatherArea) {
                    text.append(((WeatherArea) o).toCSVFormat());
                } else {
                    text.append(o.toString());
                }
                if (i != declaredFields.length - 1) {
                    text.append(", ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // TODO: program exception
            }
        }
        return text.toString();
    }
}

