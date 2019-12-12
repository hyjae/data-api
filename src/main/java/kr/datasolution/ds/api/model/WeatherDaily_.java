package kr.datasolution.ds.api.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(WeatherDaily.class)
public class WeatherDaily_ {
    public static volatile SingularAttribute<WeatherDaily, Integer> dailyId;
    public static volatile SingularAttribute<WeatherDaily, String> wDate;
    public static volatile SingularAttribute<WeatherDaily, WeatherArea> areaCode;
    public static volatile SingularAttribute<WeatherDaily, Integer> actualYn;
    public static volatile SingularAttribute<WeatherDaily, String> WDescription;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wAvgTa;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wMaxTa;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wMinTa;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wProbRn;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wSumRn;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wDdMefs;
    public static volatile SingularAttribute<WeatherDaily, Integer> wMaxWd;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wAvgWs;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wAvgRhm;
    public static volatile SingularAttribute<WeatherDaily, BigDecimal> wAvgTca;
    public static volatile SingularAttribute<WeatherDaily, Integer> aPm10;
    public static volatile SingularAttribute<WeatherDaily, Integer> aPm25;
    public static volatile SingularAttribute<WeatherDaily, Integer> aO3;
    public static volatile SingularAttribute<WeatherDaily, Integer> s01Swind;
    public static volatile SingularAttribute<WeatherDaily, Integer> s02Hrain;
    public static volatile SingularAttribute<WeatherDaily, Integer> s03Cold;
    public static volatile SingularAttribute<WeatherDaily, Integer> s04Dry;
    public static volatile SingularAttribute<WeatherDaily, Integer> s05Ssurge;
    public static volatile SingularAttribute<WeatherDaily, Integer> s06Hsea;
    public static volatile SingularAttribute<WeatherDaily, Integer> s07Typoon;
    public static volatile SingularAttribute<WeatherDaily, Integer> s08Hsnow;
    public static volatile SingularAttribute<WeatherDaily, Integer> s09YDust;
    public static volatile SingularAttribute<WeatherDaily, Integer> s10Resv;
    public static volatile SingularAttribute<WeatherDaily, Integer> s11Resv;
    public static volatile SingularAttribute<WeatherDaily, Integer> s12Hheat;
    public static volatile SingularAttribute<WeatherDaily, Timestamp> insertDdtt;
    public static volatile SingularAttribute<WeatherDaily, Timestamp> updateDdtt;

    public List<SingularAttribute> getAllAttributesButAreaCode() {
        Field[] declaredFields = WeatherDaily_.class.getDeclaredFields();
        List<Field> collect = Arrays.stream(declaredFields).collect(Collectors.toList());
        collect.forEach(
                element -> {
                    try {
                        Object o = element.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        );
        return null;
    }
}
