package cl.levelup.backend.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import cl.levelup.backend.dto.BoletaResponse;
import cl.levelup.backend.dto.CompraRequest;
import cl.levelup.backend.dto.ItemCompraRequest;
import cl.levelup.backend.entidad.Boleta;
import cl.levelup.backend.entidad.Categoria;
import cl.levelup.backend.entidad.DetalleBoleta;
import cl.levelup.backend.entidad.EstadoBoleta;
import cl.levelup.backend.entidad.Producto;
import cl.levelup.backend.entidad.Usuario;
import cl.levelup.backend.excepcion.ConflictoException;
import cl.levelup.backend.excepcion.NoEncontradoException;
import cl.levelup.backend.excepcion.SolicitudInvalidaException;
import cl.levelup.backend.repositorio.BoletaRepository;
import cl.levelup.backend.repositorio.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    private static final String TARJETA_OK = "4111 1111 1111 1111";
    private static final String TARJETA_RECHAZADA = "4111 1111 1111 1110";

    @Mock
    private BoletaRepository boletaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private BoletaService boletaService;

    private Usuario cliente;
    private Producto catan;
    private Producto carcassonne;

    @BeforeEach
    void preparar() {
        cliente = new Usuario();
        cliente.setId(3L);

        Categoria juegos = new Categoria();
        juegos.setId(1);
        juegos.setNombre("Juegos de Mesa");

        catan = producto("JM001", "Catan", 29990, 0, 25, juegos);
        carcassonne = producto("JM002", "Carcassonne", 24990, 20, 18, juegos);
    }

    @Test
    void compraAprobadaCalculaTotalConOfertaYDescuentaStock() {
        prepararCompra();

        BoletaResponse boleta = boletaService.comprar(3L, compra(TARJETA_OK,
                new ItemCompraRequest("JM001", 2), new ItemCompraRequest("JM002", 1)));

        assertThat(boleta.estado()).isEqualTo(EstadoBoleta.APROBADA);
        assertThat(boleta.total()).isEqualTo(29990 * 2 + 19992);
        assertThat(boleta.detalles()).hasSize(2);
        assertThat(boleta.detalles().get(1).precioUnitario()).isEqualTo(19992);
        assertThat(catan.getStock()).isEqualTo(23);
        assertThat(carcassonne.getStock()).isEqualTo(17);
    }

    @Test
    void pagoRechazadoGuardaBoletaSinDescontarStock() {
        prepararCompra();

        BoletaResponse boleta = boletaService.comprar(3L, compra(TARJETA_RECHAZADA, new ItemCompraRequest("JM001", 1)));

        assertThat(boleta.estado()).isEqualTo(EstadoBoleta.RECHAZADA);
        assertThat(catan.getStock()).isEqualTo(25);
    }

    @Test
    void mismoProductoRepetidoSeSumaEnUnaLinea() {
        prepararCompra();

        BoletaResponse boleta = boletaService.comprar(3L, compra(TARJETA_OK,
                new ItemCompraRequest("JM001", 1), new ItemCompraRequest("jm001", 2)));

        assertThat(boleta.detalles()).hasSize(1);
        assertThat(boleta.detalles().get(0).cantidad()).isEqualTo(3);
    }

    @Test
    void stockInsuficienteLanzaConflictoYNoGuarda() {
        when(usuarioService.buscar(3L)).thenReturn(cliente);
        when(boletaRepository.existsByNumero(anyString())).thenReturn(false);
        when(productoRepository.buscarParaActualizar("JM001")).thenReturn(Optional.of(catan));

        assertThatThrownBy(() -> boletaService.comprar(3L, compra(TARJETA_OK, new ItemCompraRequest("JM001", 26))))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("Stock insuficiente");
        verify(boletaRepository, never()).save(any());
    }

    @Test
    void productoInexistenteLanzaNoEncontrado() {
        when(usuarioService.buscar(3L)).thenReturn(cliente);
        when(boletaRepository.existsByNumero(anyString())).thenReturn(false);
        when(productoRepository.buscarParaActualizar("XX999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boletaService.comprar(3L, compra(TARJETA_OK, new ItemCompraRequest("XX999", 1))))
                .isInstanceOf(NoEncontradoException.class);
    }

    @Test
    void anularBoletaAprobadaDevuelveStock() {
        Boleta boleta = boletaCon(EstadoBoleta.APROBADA, catan, 2);
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boleta));

        BoletaResponse respuesta = boletaService.cambiarEstado(1L, EstadoBoleta.ANULADA);

        assertThat(respuesta.estado()).isEqualTo(EstadoBoleta.ANULADA);
        assertThat(catan.getStock()).isEqualTo(27);
    }

    @Test
    void noSePuedeAnularUnaBoletaRechazada() {
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boletaCon(EstadoBoleta.RECHAZADA, catan, 1)));

        assertThatThrownBy(() -> boletaService.cambiarEstado(1L, EstadoBoleta.ANULADA))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    void soloSePermiteCambiarAAnulada() {
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boletaCon(EstadoBoleta.ANULADA, catan, 1)));

        assertThatThrownBy(() -> boletaService.cambiarEstado(1L, EstadoBoleta.APROBADA))
                .isInstanceOf(SolicitudInvalidaException.class);
    }

    @Test
    void boletaAprobadaNoSeElimina() {
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boletaCon(EstadoBoleta.APROBADA, catan, 1)));

        assertThatThrownBy(() -> boletaService.eliminar(1L)).isInstanceOf(ConflictoException.class);
    }

    @Test
    void clienteNoPuedeVerBoletaDeOtro() {
        when(boletaRepository.findById(1L)).thenReturn(Optional.of(boletaCon(EstadoBoleta.APROBADA, catan, 1)));

        assertThatThrownBy(() -> boletaService.obtener(1L, 99L, false)).isInstanceOf(AccessDeniedException.class);
        assertThat(boletaService.obtener(1L, 3L, false).id()).isEqualTo(1L);
        assertThat(boletaService.obtener(1L, 99L, true).id()).isEqualTo(1L);
    }

    @Test
    void reglaDeLaPasarelaDePago() {
        assertThat(BoletaService.pagoAprobado(TARJETA_OK)).isTrue();
        assertThat(BoletaService.pagoAprobado(TARJETA_RECHAZADA)).isFalse();
        assertThat(BoletaService.pagoAprobado("1234")).isFalse();
        assertThat(BoletaService.pagoAprobado(null)).isFalse();
    }

    private void prepararCompra() {
        when(usuarioService.buscar(3L)).thenReturn(cliente);
        when(boletaRepository.existsByNumero(anyString())).thenReturn(false);
        when(productoRepository.buscarParaActualizar("JM001")).thenReturn(Optional.of(catan));
        lenient().when(productoRepository.buscarParaActualizar("JM002")).thenReturn(Optional.of(carcassonne));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private CompraRequest compra(String tarjeta, ItemCompraRequest... items) {
        return new CompraRequest("Camila", "Muñoz", "cliente@gmail.com", "Calle 1", null,
                "Valparaíso", "Viña del Mar", null, tarjeta, List.of(items));
    }

    private Boleta boletaCon(EstadoBoleta estado, Producto producto, int cantidad) {
        Boleta boleta = new Boleta();
        boleta.setId(1L);
        boleta.setEstado(estado);
        boleta.setUsuario(cliente);
        DetalleBoleta detalle = new DetalleBoleta();
        detalle.setProducto(producto);
        detalle.setNombreProducto(producto.getNombre());
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.precioFinal());
        detalle.setSubtotal(producto.precioFinal() * cantidad);
        boleta.agregarDetalle(detalle);
        return boleta;
    }

    private Producto producto(String codigo, String nombre, int precio, int oferta, int stock, Categoria categoria) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setOferta(oferta);
        p.setStock(stock);
        p.setStockCritico(5);
        p.setCategoria(categoria);
        return p;
    }
}
