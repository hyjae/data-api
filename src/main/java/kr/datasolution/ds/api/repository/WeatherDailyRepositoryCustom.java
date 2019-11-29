package kr.datasolution.ds.api.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherDailyRepositoryCustom {
    List<Object> findByColumnName(List<String> columnNames, String from, String to);
}
