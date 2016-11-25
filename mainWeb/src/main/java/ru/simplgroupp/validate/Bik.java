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
 * Bik constraint
 */
@Target( {METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = BikValidator.class)
@Documented
public @interface Bik {
    String message() default "{ru.simplgroupp.validate.Bik}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
