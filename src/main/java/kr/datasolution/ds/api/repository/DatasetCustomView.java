package kr.datasolution.ds.api.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface DatasetCustomView {
    String getDsCode();
    String getDsName();
    String getDsDesc();
    BigDecimal getDsVerMajor();
    String getDsSource();
    String getDsPeriod();
    Timestamp getDsStartDdtt();
    Timestamp getDsEndDdtt();
    Integer getDsSize();
    Timestamp getUpdateDdtt();
}
