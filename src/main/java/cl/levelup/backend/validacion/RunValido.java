package cl.levelup.backend.validacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RunValido.Validador.class)
public @interface RunValido {

    String message() default "RUN inválido. Formato: 123456785 (sin puntos ni guión).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validador implements ConstraintValidator<RunValido, String> {
        @Override
        public boolean isValid(String valor, ConstraintValidatorContext contexto) {
            return Validaciones.runValido(valor);
        }
    }
}
