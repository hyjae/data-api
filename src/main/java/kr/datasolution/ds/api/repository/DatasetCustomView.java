package kr.datasolution.ds.api.repository;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;


public interface DatasetCustomView {

    String getDsCategory(); // TODO: getting rid of it

    String getDsCode();

    String getDsName();

    String getDsDesc();

    @Value("#{(target.dsKeyword).split('\\s+')}")
    List<String> getDsKeywords();

    String getDsKeyword();

    String getDsVerMajor();

    String getDsFormat();

    String getDsSource();

    String getDsPeriod();

    String getDsStartDdtt();

    String getDsEndDdtt();

    Integer getDsSize();

    String getUpdateDdtt();

    @Value("#{(target.dsCategory)}/" + "#{(target.dsCode)}" + "/download?format=csv&from=" + "#{@timestampConverter.convert(target.dsStartDdtt)}" + "&to=" + "#{@timestampConverter.convert(target.dsEndDdtt)}")
    String getDownloadLink();
}
