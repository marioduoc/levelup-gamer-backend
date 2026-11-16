package cl.levelup.backend.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.levelup.backend.entidad.Producto;
import jakarta.persistence.LockModeType;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findAllByOrderByCodigoAsc();

    List<Producto> findByCategoriaIdOrderByCodigoAsc(Integer categoriaId);

    List<Producto> findByOfertaGreaterThanOrderByCodigoAsc(Integer oferta);

    @Query("select p from Producto p where p.stock <= p.stockCritico order by p.stock asc")
    List<Producto> buscarEnStockCritico();

    // bloquea la fila hasta que termine la compra, así dos compras no descuentan el mismo stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.codigo = :codigo")
    Optional<Producto> buscarParaActualizar(@Param("codigo") String codigo);

    boolean existsByCategoriaId(Integer categoriaId);
}
