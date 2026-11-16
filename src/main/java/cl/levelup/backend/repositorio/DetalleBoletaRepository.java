package cl.levelup.backend.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.levelup.backend.entidad.DetalleBoleta;

public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {

    boolean existsByProductoCodigo(String codigo);
}
