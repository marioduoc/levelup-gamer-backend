package cl.levelup.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import cl.levelup.backend.entidad.Boleta;
import cl.levelup.backend.entidad.DetalleBoleta;
import cl.levelup.backend.entidad.EstadoBoleta;

public record BoletaResponse(
        Long id,
        String numero,
        LocalDateTime fecha,
        Integer subtotal,
        Integer descuento,
        Integer total,
        EstadoBoleta estado,
        Long usuarioId,
        String nombreCliente,
        String apellidosCliente,
        String correoCliente,
        String calle,
        String departamento,
        String region,
        String comuna,
        String indicaciones,
        List<Detalle> detalles) {

    public record Detalle(String codigo, String nombre, Integer cantidad, Integer precioUnitario, Integer subtotal) {

        static Detalle de(DetalleBoleta d) {
            return new Detalle(d.getProducto().getCodigo(), d.getNombreProducto(), d.getCantidad(),
                    d.getPrecioUnitario(), d.getSubtotal());
        }
    }

    public static BoletaResponse de(Boleta b) {
        return new BoletaResponse(
                b.getId(),
                b.getNumero(),
                b.getFecha(),
                b.getSubtotal(),
                b.getDescuento(),
                b.getTotal(),
                b.getEstado(),
                b.getUsuario().getId(),
                b.getNombreCliente(),
                b.getApellidosCliente(),
                b.getCorreoCliente(),
                b.getCalle(),
                b.getDepartamento(),
                b.getRegion(),
                b.getComuna(),
                b.getIndicaciones(),
                b.getDetalles().stream().map(Detalle::de).toList());
    }
}
