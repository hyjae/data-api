package kr.datasolution.ds.api.repository;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.sql.Timestamp;


public interface DatasetCustomView {
    String getDsCode();
    String getDsName();
    String getDsDesc();
    String getDsKeyword();
    BigDecimal getDsVerMajor();
    String getDsFormat();
    String getDsSource();
    String getDsPeriod();
    Timestamp getDsStartDdtt();
    Timestamp getDsEndDdtt();
    Integer getDsSize();
    Timestamp getUpdateDdtt();
}
