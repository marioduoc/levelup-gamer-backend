package cl.levelup.backend.dto;

import cl.levelup.backend.entidad.Producto;

public record ProductoResponse(
        String codigo,
        String nombre,
        String descripcion,
        Integer precio,
        Integer oferta,
        Integer precioFinal,
        Integer stock,
        Integer stockCritico,
        boolean stockBajo,
        String imagen,
        Integer categoriaId,
        String categoria) {

    public static ProductoResponse de(Producto p) {
        return new ProductoResponse(
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getPrecio(),
                p.getOferta(),
                p.precioFinal(),
                p.getStock(),
                p.getStockCritico(),
                p.enStockCritico(),
                p.getImagen(),
                p.getCategoria().getId(),
                p.getCategoria().getNombre());
    }
}
