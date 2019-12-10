package kr.datasolution.ds.api.util;

import javax.persistence.AttributeConverter;

public class DateFormatConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String s) {
        return s.replace("-", "");
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return s.replace("-", "");
    }
}

