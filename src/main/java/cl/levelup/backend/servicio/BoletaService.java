package cl.levelup.backend.servicio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.levelup.backend.dto.BoletaResponse;
import cl.levelup.backend.dto.CompraRequest;
import cl.levelup.backend.dto.ItemCompraRequest;
import cl.levelup.backend.entidad.Boleta;
import cl.levelup.backend.entidad.DetalleBoleta;
import cl.levelup.backend.entidad.EstadoBoleta;
import cl.levelup.backend.entidad.Producto;
import cl.levelup.backend.entidad.Usuario;
import cl.levelup.backend.excepcion.ConflictoException;
import cl.levelup.backend.excepcion.NoEncontradoException;
import cl.levelup.backend.excepcion.SolicitudInvalidaException;
import cl.levelup.backend.repositorio.BoletaRepository;
import cl.levelup.backend.repositorio.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoletaService {

    private static final DateTimeFormatter FORMATO_NUMERO = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioService usuarioService;

    // pasarela simulada (igual que en la EP2): 16 dígitos y que no termine en 0
    public static boolean pagoAprobado(String tarjeta) {
        String limpio = tarjeta == null ? "" : tarjeta.replaceAll("\\s", "");
        return limpio.matches("\\d{16}") && !limpio.endsWith("0");
    }

    @Transactional
    public BoletaResponse comprar(Long usuarioId, CompraRequest compra) {
        Usuario usuario = usuarioService.buscar(usuarioId);
        boolean aprobado = pagoAprobado(compra.tarjeta());

        Boleta boleta = new Boleta();
        boleta.setUsuario(usuario);
        boleta.setFecha(LocalDateTime.now());
        boleta.setNumero(generarNumero(boleta.getFecha()));
        boleta.setEstado(aprobado ? EstadoBoleta.APROBADA : EstadoBoleta.RECHAZADA);
        copiarDatosEnvio(compra, boleta);

        int total = 0;
        for (Map.Entry<String, Integer> item : agruparItems(compra.items()).entrySet()) {
            Producto producto = productoRepository.buscarParaActualizar(item.getKey())
                    .orElseThrow(() -> new NoEncontradoException("No existe el producto " + item.getKey() + "."));
            int cantidad = item.getValue();
            if (producto.getStock() < cantidad) {
                throw new ConflictoException("Stock insuficiente para '" + producto.getNombre()
                        + "'. Disponible: " + producto.getStock() + ".");
            }

            DetalleBoleta detalle = new DetalleBoleta();
            detalle.setProducto(producto);
            detalle.setNombreProducto(producto.getNombre());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.precioFinal());
            detalle.setSubtotal(producto.precioFinal() * cantidad);
            boleta.agregarDetalle(detalle);
            total += detalle.getSubtotal();

            if (aprobado) {
                producto.setStock(producto.getStock() - cantidad);
            }
        }
        boleta.setTotal(total);
        return BoletaResponse.de(boletaRepository.save(boleta));
    }

    @Transactional(readOnly = true)
    public List<BoletaResponse> listar() {
        return boletaRepository.findAllByOrderByFechaDesc().stream().map(BoletaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<BoletaResponse> listarDeUsuario(Long usuarioId) {
        return boletaRepository.findByUsuarioIdOrderByFechaDesc(usuarioId).stream().map(BoletaResponse::de).toList();
    }

    // un cliente solo puede ver sus propias boletas
    @Transactional(readOnly = true)
    public BoletaResponse obtener(Long id, Long usuarioId, boolean puedeVerTodas) {
        Boleta boleta = buscar(id);
        if (!puedeVerTodas && !boleta.getUsuario().getId().equals(usuarioId)) {
            throw new AccessDeniedException("La boleta no pertenece al usuario.");
        }
        return BoletaResponse.de(boleta);
    }

    // solo se permite anular una boleta aprobada, y se devuelve el stock
    @Transactional
    public BoletaResponse cambiarEstado(Long id, EstadoBoleta nuevoEstado) {
        Boleta boleta = buscar(id);
        if (nuevoEstado != EstadoBoleta.ANULADA) {
            throw new SolicitudInvalidaException("Solo se puede cambiar el estado a ANULADA.");
        }
        if (boleta.getEstado() != EstadoBoleta.APROBADA) {
            throw new ConflictoException("Solo se pueden anular boletas aprobadas.");
        }
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }
        boleta.setEstado(EstadoBoleta.ANULADA);
        return BoletaResponse.de(boleta);
    }

    @Transactional
    public void eliminar(Long id) {
        Boleta boleta = buscar(id);
        if (boleta.getEstado() == EstadoBoleta.APROBADA) {
            throw new ConflictoException("Una boleta aprobada no se elimina, primero hay que anularla.");
        }
        boletaRepository.delete(boleta);
    }

    private Boleta buscar(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la boleta " + id + "."));
    }

    // si el mismo producto viene dos veces se suman las cantidades
    private Map<String, Integer> agruparItems(List<ItemCompraRequest> items) {
        Map<String, Integer> agrupados = new LinkedHashMap<>();
        for (ItemCompraRequest item : items) {
            agrupados.merge(item.codigo().trim().toUpperCase(), item.cantidad(), Integer::sum);
        }
        return agrupados;
    }

    private String generarNumero(LocalDateTime fecha) {
        String numero;
        do {
            int correlativo = ThreadLocalRandom.current().nextInt(1000, 10000);
            numero = fecha.format(FORMATO_NUMERO) + "-" + correlativo;
        } while (boletaRepository.existsByNumero(numero));
        return numero;
    }

    private void copiarDatosEnvio(CompraRequest compra, Boleta boleta) {
        boleta.setNombreCliente(compra.nombre().trim());
        boleta.setApellidosCliente(compra.apellidos().trim());
        boleta.setCorreoCliente(compra.correo().trim().toLowerCase());
        boleta.setCalle(compra.calle().trim());
        boleta.setDepartamento(compra.departamento());
        boleta.setRegion(compra.region());
        boleta.setComuna(compra.comuna());
        boleta.setIndicaciones(compra.indicaciones());
    }
}
