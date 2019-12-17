package kr.datastation.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = AreaConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAreaCode {
    String value() default "10";
    String message() default "Invalid AreaCode Value";
    Class<?>[] groups() default {}; // empty collection
    Class<? extends Payload>[] payload() default {}; //?
}
