package cl.levelup.backend.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.levelup.backend.dto.ProductoRequest;
import cl.levelup.backend.dto.ProductoResponse;
import cl.levelup.backend.entidad.Producto;
import cl.levelup.backend.excepcion.ConflictoException;
import cl.levelup.backend.excepcion.NoEncontradoException;
import cl.levelup.backend.repositorio.DetalleBoletaRepository;
import cl.levelup.backend.repositorio.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final DetalleBoletaRepository detalleBoletaRepository;
    private final CategoriaService categoriaService;

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(Integer categoriaId, boolean soloOfertas) {
        List<Producto> productos;
        if (soloOfertas) {
            productos = productoRepository.findByOfertaGreaterThanOrderByCodigoAsc(0);
        } else if (categoriaId != null) {
            productos = productoRepository.findByCategoriaIdOrderByCodigoAsc(categoriaId);
        } else {
            productos = productoRepository.findAllByOrderByCodigoAsc();
        }
        return productos.stream().map(ProductoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(String codigo) {
        return ProductoResponse.de(buscar(codigo));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> stockCritico() {
        return productoRepository.buscarEnStockCritico().stream().map(ProductoResponse::de).toList();
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest datos) {
        if (productoRepository.existsById(datos.codigo())) {
            throw new ConflictoException("Ya existe un producto con el código " + datos.codigo() + ".");
        }
        Producto producto = new Producto();
        producto.setCodigo(datos.codigo());
        copiarDatos(datos, producto);
        return ProductoResponse.de(productoRepository.save(producto));
    }

    // el código es la llave, no se cambia al editar
    @Transactional
    public ProductoResponse actualizar(String codigo, ProductoRequest datos) {
        Producto producto = buscar(codigo);
        copiarDatos(datos, producto);
        return ProductoResponse.de(producto);
    }

    @Transactional
    public void eliminar(String codigo) {
        Producto producto = buscar(codigo);
        if (detalleBoletaRepository.existsByProductoCodigo(codigo)) {
            throw new ConflictoException("El producto " + codigo
                    + " tiene ventas registradas y no se puede eliminar. Deja su stock en 0.");
        }
        productoRepository.delete(producto);
    }

    private void copiarDatos(ProductoRequest datos, Producto producto) {
        producto.setNombre(datos.nombre().trim());
        producto.setDescripcion(datos.descripcion());
        producto.setPrecio(datos.precio());
        producto.setOferta(datos.oferta() == null ? 0 : datos.oferta());
        producto.setStock(datos.stock());
        producto.setStockCritico(datos.stockCritico());
        producto.setImagen(datos.imagen());
        producto.setCategoria(categoriaService.buscar(datos.categoriaId()));
    }

    private Producto buscar(String codigo) {
        return productoRepository.findById(codigo)
                .orElseThrow(() -> new NoEncontradoException("No existe el producto " + codigo + "."));
    }
}
