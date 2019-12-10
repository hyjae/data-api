package kr.datasolution.ds.api.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;

@Converter
public class BigDecimalConverter implements AttributeConverter<BigDecimal, Long> {
    /**
     * https://stackoverflow.com/questions/47703481/jpa-save-bigdecimal-as-integer-in-database
     * @param value BigDecimal
     * @return Long
     */

    @Override
    public Long convertToDatabaseColumn(BigDecimal value) {
        if (value == null) {
            return null;
        } else {
            return value.multiply(BigDecimal.valueOf(100)).longValue();
        }
    }

    @Override
    public BigDecimal convertToEntityAttribute(Long value) {
        if (value == null) {
            return null;
        } else {
            return new BigDecimal(value).divide(BigDecimal.valueOf(100));
        }
    }
}
