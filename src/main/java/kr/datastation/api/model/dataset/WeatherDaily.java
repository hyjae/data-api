package kr.datastation.api.model.dataset;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "weather_daily", schema = "dataset_a")
public class WeatherDaily implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_id")
    private Integer dailyId;

    @Column(name = "w_date")
    private String wDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_code")
    private WeatherArea areaCode;

    @Column(name = "actual_yn")
    private Integer actualYn;

    @Column(name = "w_description")
    private String wDescription;

    @Column(name = "w_avg_ta")
    private BigDecimal wAvgTa;

    @Column(name = "w_max_ta")
    private BigDecimal wMaxTa;

    @Column(name = "w_min_ta")
    private BigDecimal wMinTa;

    @Column(name = "w_prob_rn")
    private BigDecimal wProbRn;

    @Column(name = "w_sum_rn")
    private BigDecimal wSumRn;

    @Column(name = "w_dd_mefs")
    private BigDecimal wDdMefs;

    @Column(name = "w_max_wd")
    private Integer wMaxWd;

    @Column(name = "w_avg_ws")
    private BigDecimal wAvgWs;

    @Column(name = "w_avg_rhm")
    private BigDecimal wAvgRhm;

    @Column(name = "w_avg_tca")
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
    private Integer s09Ydust;

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

    public String toCSV() {
        return this.wDate + "," + this.areaCode.getAreaCode() + "," + this.areaCode.getMainName() + "," + this.areaCode.getSubName() + "," + this.areaCode.getCityName() + "," + this.actualYn + "," + this.wDescription + "," + this.wAvgTa + "," + this.wMaxTa + "," + this.wMinTa + "," + this.wProbRn + "," + this.wSumRn + "," + this.wDdMefs + "," + this.wMaxWd + "," + this.wAvgWs + "," + this.wAvgRhm + "," + this.wAvgTca + "," + this.aPm10 + "," + this.aPm25 + "," + this.aO3 + "," + this.s01Swind + "," + this.s02Hrain + "," + this.s03Cold + ", " + this.s04Dry + "," + this.s05Ssurge + "," + this.s06Hsea + "," + this.s07Typoon + "," + this.s08Hsnow + "," + this.s09Ydust + "," + this.s10Resv + "," + this.s11Resv + "," + this.s12Hheat;
    }

//    public String toCSV() {
//        StringBuilder text = new StringBuilder();
//        Field[] declaredFields = WeatherDaily.class.getDeclaredFields();
//
//        for (int i = 1; i < declaredFields.length; ++i) { // skip index
//            try {
//                Object o = declaredFields[i].get(this);
//                if (o == null) {
//                    text.append("NULL");
//                } else if (o instanceof Date) {
//                    Format formatter = new SimpleDateFormat("yyyyMMdd");
//                    String dateFormatString = formatter.format(o);
//                    text.append(dateFormatString);
//                } else if (o instanceof WeatherArea) {
//                    text.append(((WeatherArea) o).toCSV());
//                } else {
//                    text.append(o.toString());
//                }
//                if (i != declaredFields.length - 1) {
//                    text.append(", ");
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace(); // TODO: program exception
//            }
//        }
//        return text.toString();
//    }
}

