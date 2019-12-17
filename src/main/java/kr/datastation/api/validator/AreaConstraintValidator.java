package kr.datastation.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AreaConstraintValidator implements ConstraintValidator<ValidEntityName, String> {

    @Override
    public void initialize(ValidEntityName validEntityName) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return true;
    }

}
