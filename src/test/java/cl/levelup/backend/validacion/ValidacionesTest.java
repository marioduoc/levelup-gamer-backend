package cl.levelup.backend.validacion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ValidacionesTest {

    @Test
    void runConDigitoVerificadorCorrecto() {
        assertThat(Validaciones.runValido("111111111")).isTrue();
        assertThat(Validaciones.runValido("123456785")).isTrue();
        assertThat(Validaciones.runValido("10000013K")).isTrue();
    }

    @Test
    void runInvalido() {
        assertThat(Validaciones.runValido("123456789")).isFalse();
        assertThat(Validaciones.runValido("12.345.678-5")).isFalse();
        assertThat(Validaciones.runValido("")).isFalse();
        assertThat(Validaciones.runValido(null)).isFalse();
    }

    @Test
    void soloDominiosPermitidos() {
        assertThat(Validaciones.correoPermitido("ana@duoc.cl")).isTrue();
        assertThat(Validaciones.correoPermitido("profe@profesor.duoc.cl")).isTrue();
        assertThat(Validaciones.correoPermitido("Ana@GMAIL.com")).isTrue();
        assertThat(Validaciones.correoPermitido("ana@hotmail.com")).isFalse();
        assertThat(Validaciones.correoPermitido("@gmail.com")).isFalse();
        assertThat(Validaciones.correoPermitido("sin-arroba")).isFalse();
    }

    @Test
    void correosConDescuentoDuoc() {
        assertThat(Validaciones.correoDuoc("ana@duoc.cl")).isTrue();
        assertThat(Validaciones.correoDuoc("PROFE@profesor.duoc.cl")).isTrue();
        assertThat(Validaciones.correoDuoc("ana@gmail.com")).isFalse();
        assertThat(Validaciones.correoDuoc(null)).isFalse();
    }

    @Test
    void mayorDeEdad() {
        assertThat(Validaciones.mayorDeEdad(LocalDate.now().minusYears(18))).isTrue();
        assertThat(Validaciones.mayorDeEdad(LocalDate.now().minusYears(18).plusDays(1))).isFalse();
        assertThat(Validaciones.mayorDeEdad(null)).isFalse();
    }
}
