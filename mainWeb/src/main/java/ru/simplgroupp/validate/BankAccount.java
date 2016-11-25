package ru.simplgroupp.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Bank account constraint
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = BankAccountValidator.class)
@Documented
public @interface BankAccount {

    String message() default "{ru.simplgroupp.validate.BankAccount}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String valueField();

    String bikField();
}
