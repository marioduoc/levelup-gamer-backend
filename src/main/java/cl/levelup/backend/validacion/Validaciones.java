package cl.levelup.backend.validacion;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public final class Validaciones {

    public static final List<String> DOMINIOS_CORREO = List.of("duoc.cl", "profesor.duoc.cl", "gmail.com");

    private Validaciones() {
    }

    // RUN sin puntos ni guion, digito verificador con modulo 11
    public static boolean runValido(String run) {
        if (run == null) {
            return false;
        }
        String limpio = run.trim().toUpperCase();
        if (!limpio.matches("^[0-9]{6,8}[0-9K]$")) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);

        int suma = 0;
        int multiplo = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplo;
            multiplo = multiplo == 7 ? 2 : multiplo + 1;
        }
        int resto = 11 - (suma % 11);
        char esperado = resto == 11 ? '0' : resto == 10 ? 'K' : Character.forDigit(resto, 10);
        return dv == esperado;
    }

    public static boolean correoPermitido(String correo) {
        if (correo == null || correo.length() > 100) {
            return false;
        }
        String[] partes = correo.split("@");
        return partes.length == 2 && !partes[0].isBlank()
                && DOMINIOS_CORREO.contains(partes[1].toLowerCase());
    }

    // correos con descuento del 20 %
    public static boolean correoDuoc(String correo) {
        return correo != null && correo.toLowerCase().matches(".+@(duoc\\.cl|profesor\\.duoc\\.cl)$");
    }

    public static boolean mayorDeEdad(LocalDate fechaNacimiento) {
        return fechaNacimiento != null
                && Period.between(fechaNacimiento, LocalDate.now()).getYears() >= 18;
    }
}
