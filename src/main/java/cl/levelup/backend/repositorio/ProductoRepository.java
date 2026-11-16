package cl.levelup.backend.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.levelup.backend.entidad.Producto;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findByCategoriaId(Integer categoriaId);

    List<Producto> findByOfertaGreaterThan(Integer oferta);

    @Query("select p from Producto p where p.stock <= p.stockCritico")
    List<Producto> buscarEnStockCritico();

    boolean existsByCategoriaId(Integer categoriaId);
}
