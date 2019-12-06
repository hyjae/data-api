package kr.datasolution.ds.api.validator;

import kr.datasolution.ds.api.domain.TimePoint;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented // ?
public @interface DateRequestParam {
    TimePoint point();
}
