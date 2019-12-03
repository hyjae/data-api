package kr.datasolution.ds.api.domain;

import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "weather_area_info")
public class WeatherArea implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "main_name")
    private String mainName;

    @Column(name = "sub_name")
    private String subName;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "other_cities")
    private String otherCities;

    @Column(name = "w1_stnIds")
    private Integer w1StnIds;

    @Column(name = "w1_stnNm")
    private String w1StnNm;

    @Column(name = "w2_d0_regid")
    private String w2D0RegId;

    @Column(name = "w2_d3_landid")
    private String w2D3LandId;

    @Column(name = "w2_d3_tempid")
    private String w3D3TempId;

    @Column(name = "w2_region_id")
    private String w2RegionId;

    @Column(name = "a1_sidoNameReq")
    private String a1SiDoNameReq;

    @Column(name = "a1_sidoNameResp")
    private String a1SiDoNameResp;

    @Column(name = "a2_sidoNameResp")
    private String a2SiDoNameResp;

    @Column(name = "s1_areaCode")
    private String s1AreaCode;

    @Column(name = "insert_ddtt")
    private Timestamp insertDdTt;

    @Column(name = "update_ddtt")
    private Timestamp updateDdTt;
}
