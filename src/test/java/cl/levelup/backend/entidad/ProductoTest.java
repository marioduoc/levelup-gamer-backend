package cl.levelup.backend.entidad;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductoTest {

    @Test
    void precioFinalAplicaLaOferta() {
        Producto p = new Producto();
        p.setPrecio(24990);
        p.setOferta(20);
        assertThat(p.precioFinal()).isEqualTo(19992);

        p.setOferta(0);
        assertThat(p.precioFinal()).isEqualTo(24990);
    }

    @Test
    void stockCriticoCuandoLlegaAlMinimo() {
        Producto p = new Producto();
        p.setStockCritico(5);

        p.setStock(5);
        assertThat(p.enStockCritico()).isTrue();

        p.setStock(6);
        assertThat(p.enStockCritico()).isFalse();
    }
}
