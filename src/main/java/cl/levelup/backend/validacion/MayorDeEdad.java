package cl.levelup.backend.validacion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MayorDeEdad.Validador.class)
public @interface MayorDeEdad {

    String message() default "Debes ser mayor de 18 años.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validador implements ConstraintValidator<MayorDeEdad, LocalDate> {
        @Override
        public boolean isValid(LocalDate valor, ConstraintValidatorContext contexto) {
            // si viene vacío lo reporta @NotNull
            return valor == null || Validaciones.mayorDeEdad(valor);
        }
    }
}
