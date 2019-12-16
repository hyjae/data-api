package kr.datasolution.ds.api.repository;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;


public interface DatasetCustomView {

    String getDsCode();

    String getDsName();

    String getDsDesc();

    @Value("#{(target.dsKeyword).split('\\s+')}")
    List<String> getDsKeywords();

    String getDsVerMajor();

    String getDsFormat();

    String getDsSource();

    String getDsPeriod();

    @Value("#{(@timestampConverter.convert(target.dsStartDdtt))}")
    String getDsStartDate();

    @Value("#{(@timestampConverter.convert(target.dsEndDdtt))}")
    String getDsEndDate();

    Integer getDsSize();

    @Value("#{(@timestampConverter.convert(target.updateDdtt))}")
    String getUpdateDate();

    @Value("#{(target.dsCategory)}/" + "#{(target.dsCode)}" + "/download?format=csv&from=" + "#{@timestampConverter.convert(target.dsStartDdtt)}" + "&to=" + "#{@timestampConverter.convert(target.dsEndDdtt)}")
    String getDownloadLink();
}
