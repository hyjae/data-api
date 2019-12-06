package kr.datasolution.ds.api.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "calendar")
public class Calendar {

    @Id
    @Column(name = "sol_ymd")
    String solYmd;

    @Column(name = "sol_year")
    Integer solYear;

    @Column(name = "sol_month")
    Integer solMonth;

    @Column(name = "sol_day")
    Integer solDay;

    @Column(name = "sol_nday")
    Integer solNday;

    @Column(name = "sol_leapyear")
    Integer solLeapyear;

    @Column(name = "sol_week")
    Integer solWeek;

    @Column(name = "lun_year")
    Integer lunYear;

    @Column(name = "lun_month")
    Integer lunMonth;

    @Column(name = "lun_day")
    Integer lunDay;

    @Column(name = "lun_nday")
    Integer lunNday;

    @Column(name = "lun_leapmonth")
    Integer lunLeapmonth;

    @Column(name = "lun_secha")
    String lunSecha;

    @Column(name = "lun_wolgeon")
    String lunWolgeon;

    @Column(name = "lun_iljin")
    String lunIljin;

    @Column(name = "date_kind")
    Integer dateKind;

    @Column(name = "is_special")
    Integer isSpecial;

    @Column(name = "is_holiday")
    Integer isHoliday;

    @Column(name = "date_name")
    String dateName;

    @Column(name = "is_special_us")
    Integer isSpecialUs;

    @Column(name = "is_holiday_us")
    Integer isHolidayUs;

    @Column(name = "date_name_us")
    String dateMameUs;

    @Column(name = "is_special_jp")
    Integer isSpecialJp;

    @Column(name = "is_holiday_jp")
    Integer isHolidayJp;

    @Column(name = "date_name_jp")
    String dateNameJp;

    @Column(name = "is_special_cn")
    Integer isSpecialCn;

    @Column(name = "is_holiday_cn")
    Integer isHolidayCn;

    @Column(name = "date_name_cn")
    String dateNameCn;

    @Column(name = "insert_ddtt")
    Timestamp insertDdtt;

    @Column(name = "update_ddtt")
    Timestamp updateDdtt;

    // TODO: func
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] declaredFields = Calendar.class.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; ++i) {
            try {
                Object o = declaredFields[i].get(this);
                if (o == null) {
                    stringBuilder.append("NULL");
                } else {
                    stringBuilder.append(o.toString());
                }
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
