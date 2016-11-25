package ru.simplgroupp.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Kpp constraint
 */
@Target( {METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = KppValidator.class)
@Documented
public @interface Kpp {
    String message() default "{ru.simplgroupp.validate.Kpp}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
