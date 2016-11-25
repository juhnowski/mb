package ru.simplgroupp.validate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Corr account validator
 */
public class CorrAccountValidator implements ConstraintValidator<CorrAccount, Object> {

    private String valueField;

    private String bikField;

    private static final int[] MASK = new int[]{7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1, 3, 7, 1};

    @Override
    public void initialize(CorrAccount corrAccount) {
        this.valueField = corrAccount.valueField();
        this.bikField = corrAccount.bikField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            String corrAccount = BeanUtils.getProperty(object, valueField);
            String bik = BeanUtils.getProperty(object, bikField);

            if (corrAccount == null || bik == null) {
                return true;
            }

            boolean isValid = false;
            if (StringUtils.isNumeric(corrAccount) && corrAccount.length() == 20) {
                String s = "0" + bik.substring(4, 6) + corrAccount;

                int sum = 0;
                for (int i = 0; i < s.length(); i++) {
                    sum = (sum + Integer.valueOf("" + s.charAt(i)) * MASK[i]) % 10;
                }

                isValid = sum % 10 == 0;
            }

            if (!isValid) {
                addConstraintViolation(context, "{ru.simplgroupp.validate.CorrAccount}", valueField);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message, String field) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addNode(field).addConstraintViolation();
    }
}
