package cl.levelup.backend.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.levelup.backend.entidad.Boleta;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Boleta> findAllByOrderByFechaDesc();

    boolean existsByNumero(String numero);

    boolean existsByUsuarioId(Long usuarioId);
}
