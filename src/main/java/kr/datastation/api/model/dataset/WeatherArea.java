package kr.datastation.api.model.dataset;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "weather_area_info", schema = "dataset_a")
public class WeatherArea implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @CsvBindByName
    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "main_name")
    @CsvBindByName
    private String mainName;

    @CsvBindByName
    @Column(name = "sub_name")
    private String subName;

    @CsvBindByName
    @Column(name = "city_name")
    private String cityName;

    @CsvBindByName
    @Column(name = "other_cities")
    private String otherCities;

    @CsvBindByName
    @Column(name = "w1_stnIds")
    private Integer w1StnIds;

    @CsvBindByName
    @Column(name = "w1_stnNm")
    private String w1StnNm;

    @CsvBindByName
    @Column(name = "w2_d0_regid")
    private String w2D0RegId;

    @CsvBindByName
    @Column(name = "w2_d3_landid")
    private String w2D3LandId;

    @CsvBindByName
    @Column(name = "w2_d3_tempid")
    private String w3D3TempId;

    @CsvBindByName
    @Column(name = "w2_region_id")
    private String w2RegionId;

    @CsvBindByName
    @Column(name = "a1_sidoNameReq")
    private String a1SiDoNameReq;

    @CsvBindByName
    @Column(name = "a1_sidoNameResp")
    private String a1SiDoNameResp;

    @CsvBindByName
    @Column(name = "a2_sidoNameResp")
    private String a2SiDoNameResp;

    @CsvBindByName
    @Column(name = "s1_areaCode")
    private String s1AreaCode;

    @CsvBindByName
    @Column(name = "insert_ddtt")
    private Timestamp insertDdTt;

    @CsvBindByName
    @Column(name = "update_ddtt")
    private Timestamp updateDdTt;

    public String toCSV() {
        return this.getAreaCode() + ", " + this.getMainName() + ", " + this.getSubName() + ", " + this.getCityName();
    }
}
