package cl.levelup.backend.dto;

import cl.levelup.backend.entidad.Categoria;

public record CategoriaResponse(Integer id, String nombre) {

    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre());
    }
}
