package kr.datastation.api.validator;

import kr.datastation.api.vo.TimePoint;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented // ?
public @interface DateRequestParam {
    TimePoint point();
}
