package kr.datasolution.ds.api.validator;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class IntervalConstraintValidator implements ConstraintValidator<ValidInterval, String> {

    private String interval;

    @Override
    public void initialize(ValidInterval validInterval) {
        this.interval = validInterval.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        DateHistogramInterval dateHistogramInterval = new DateHistogramInterval(s);
        for (
                Field field : dateHistogramInterval.getClass().getDeclaredFields()) {
            try {
//                field.setAccessible(true);
                Object o = field.get(dateHistogramInterval);
                if (s.equals(o.toString()))
                    return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
