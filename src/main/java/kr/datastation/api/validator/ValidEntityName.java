package kr.datastation.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = EntityNameConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEntityName {
    String value() default "etcNamedEntity";
    String message() default "Invalid Entity Name";
    Class<?>[] groups() default {}; // empty collection
    Class<? extends Payload>[] payload() default {}; //?
}
