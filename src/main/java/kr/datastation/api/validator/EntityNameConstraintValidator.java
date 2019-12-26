package kr.datastation.api.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class EntityNameConstraintValidator implements ConstraintValidator<ValidEntityName, String> {

    final List<String> namedEntityList = Arrays.asList("organizationNamedEntity", "locationNamedEntity",
            "personNamedEntity", "etcNamedEntity", "totalNamedEntity");

    private String entityName;

    @Override
    public void initialize(ValidEntityName validEntityName) {
        this.entityName = validEntityName.value();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!namedEntityList.contains(s)) {
            String namedEntity = namedEntityList.stream().collect(Collectors.joining(", "));
            StringBuilder message = new StringBuilder()
                    .append(s)
                    .append(" not in: ")
                    .append(namedEntity);
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(message.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
