package kr.datastation.api.repository.dataset;


import org.springframework.beans.factory.annotation.Value;

public interface CalendarCustomView {
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

    @Value("#{target.solYmd + ', ' + target.solYear + ', ' + target.solMonth + ', ' + target.solDay + ', ' + target.solNday + ', ' + target.solLeapyear + ', ' + target.solWeek + ', ' + target.lunYear + ', ' + target.lunMonth + ', ' + target.lunDay + ', ' + target.lunNday + ', ' + target.lunLeapmonth + ', ' + target.lunSecha + ', ' + target.lunWolgeon + ', ' + target.lunIljin + ', ' + target.dateKind + ', ' + target.isSpecial + ', ' + target.isHoliday + ', ' + target.dateName + ', ' + target.isSpecialUs + ', ' + target.isHolidayUs + ', ' + target.dateMameUs + ', ' + target.isSpecialJp + ', ' + target.isHolidayJp + ', ' + target.dateNameJp + ', ' + target.isSpecialCn + ', ' + target.isHolidayCn + ', ' + target.dateNameCn}")
    String getCalendarCSVFormat();
}
