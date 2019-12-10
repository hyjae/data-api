package kr.datasolution.ds.api.domain;


import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(WeatherArea.class)
public class WeatherArea_ {
    public static volatile SingularAttribute<WeatherArea, Integer> areaCode;
    public static volatile SingularAttribute<WeatherArea, String> mainName;
    public static volatile SingularAttribute<WeatherArea, String> subName;
    public static volatile SingularAttribute<WeatherArea, String> cityName;
    public static volatile SingularAttribute<WeatherArea, String> otherCities;
    public static volatile SingularAttribute<WeatherArea, Integer> w1StnIds;
    public static volatile SingularAttribute<WeatherArea, String> w1StnNm;
    public static volatile SingularAttribute<WeatherArea, String> w2D0RegId;
    public static volatile SingularAttribute<WeatherArea, String> w2D3LandId;
    public static volatile SingularAttribute<WeatherArea, String> w3D3TempId;
    public static volatile SingularAttribute<WeatherArea, String> w2RegionId;
    public static volatile SingularAttribute<WeatherArea, String> a1SiDoNameReq;
    public static volatile SingularAttribute<WeatherArea, String> a1SiDoNameResp;
    public static volatile SingularAttribute<WeatherArea, String> a2SiDoNameResp;
    public static volatile SingularAttribute<WeatherArea, String> s1AreaCode;
    public static volatile SingularAttribute<WeatherArea, Timestamp> insertDdTt;
    public static volatile SingularAttribute<WeatherArea, Timestamp> updateDdTt;

    public String convertToWeatherAreaMetamodel(List<String> colNames) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] declaredFields = WeatherDaily.class.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; ++i) {
            try {
                Object o = declaredFields[i].get(this);
                stringBuilder.append(o.toString());
                if (i != declaredFields.length - 1) {
                    stringBuilder.append(", ");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // TODO: program exception
            }
        }
        return stringBuilder.toString();
    }
}
