package kr.datasolution.ds.api.repository;

import kr.datasolution.ds.api.domain.Calendar;

@Projection(
        name = "customCalander",
        types = { Calendar.class })
public interface CalendarWithSelectedColumns {
    String getSolYmd();
    String getSolYear();
    Integer getSolMonth();
    Integer getSolDay();
    Integer getSolNday();
    Integer getSolLeapyear();
    Integer getSolWeek();
    Integer getLunYear();
    Integer getLunMonth();
    Integer getLunDay();
    Integer getLunNday();
    Integer getLunLeapmonth();
    String getLunSecha();
    String getLunWolgeon();
    String getLunIljin();
    Integer getDateKind();
    Integer getIsSpecial();
    Integer getIsHoliday();
    String getDateName();
    Integer getIsSpecialUs();
    Integer getIsHolidayUs();
    String getDateMameUs();
    Integer getIsSpecialJp();
    Integer getIsHolidayJp();
    String getDateNameJp();
    Integer getIsSpecialCn();
    Integer getIsHolidayCn();
    String getDateNameCn();
}
