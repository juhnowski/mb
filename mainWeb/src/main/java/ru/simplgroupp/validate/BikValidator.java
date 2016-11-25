package ru.simplgroupp.validate;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Bik validator
 */
public class BikValidator implements ConstraintValidator<Bik, String> {

    @Override
    public void initialize(Bik bik) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        if (s.length() != 9 || !s.startsWith("04") || !StringUtils.isNumeric(s)) {
            return false;
        }

        return true;
    }
}
