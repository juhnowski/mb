package ru.simplgroupp.validate;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Kpp validator
 */
public class KppValidator implements ConstraintValidator<Kpp, String> {

    @Override
    public void initialize(Kpp kpp) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        if (s.length() != 9 || !StringUtils.isNumeric(s)) {
            return false;
        }

        return true;
    }
}
