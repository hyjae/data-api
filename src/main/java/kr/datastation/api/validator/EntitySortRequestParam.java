package kr.datastation.api.validator;

import kr.datastation.api.vo.EntitySort;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented // ?
public @interface EntitySortRequestParam {
    EntitySort entityOrder() default EntitySort.DATE_ASC;
}
