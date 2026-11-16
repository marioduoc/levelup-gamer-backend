package cl.levelup.backend.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.levelup.backend.dto.CategoriaRequest;
import cl.levelup.backend.dto.CategoriaResponse;
import cl.levelup.backend.entidad.Categoria;
import cl.levelup.backend.excepcion.ConflictoException;
import cl.levelup.backend.excepcion.NoEncontradoException;
import cl.levelup.backend.repositorio.CategoriaRepository;
import cl.levelup.backend.repositorio.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Integer id) {
        return CategoriaResponse.de(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest datos) {
        String nombre = datos.nombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe la categoría '" + nombre + "'.");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return CategoriaResponse.de(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Integer id, CategoriaRequest datos) {
        Categoria categoria = buscar(id);
        String nombre = datos.nombre().trim();
        if (!categoria.getNombre().equalsIgnoreCase(nombre) && categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe la categoría '" + nombre + "'.");
        }
        categoria.setNombre(nombre);
        return CategoriaResponse.de(categoria);
    }

    @Transactional
    public void eliminar(Integer id) {
        Categoria categoria = buscar(id);
        if (productoRepository.existsByCategoriaId(id)) {
            throw new ConflictoException("No se puede eliminar '" + categoria.getNombre() + "' porque tiene productos.");
        }
        categoriaRepository.delete(categoria);
    }

    Categoria buscar(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la categoría " + id + "."));
    }
}
