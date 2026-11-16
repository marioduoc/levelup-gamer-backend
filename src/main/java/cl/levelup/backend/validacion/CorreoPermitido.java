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
@Constraint(validatedBy = CorreoPermitido.Validador.class)
public @interface CorreoPermitido {

    String message() default "Usa un correo @duoc.cl, @profesor.duoc.cl o @gmail.com.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validador implements ConstraintValidator<CorreoPermitido, String> {
        @Override
        public boolean isValid(String valor, ConstraintValidatorContext contexto) {
            return Validaciones.correoPermitido(valor);
        }
    }
}
