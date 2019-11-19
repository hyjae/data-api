package kr.datasolution.ds.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IntervalConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInterval {
    String value() default "1w";
    String message() default "Invalid Interval Value";
    Class<?>[] groups() default {}; // empty collection
    Class<? extends Payload>[] payload() default {}; //?
}
