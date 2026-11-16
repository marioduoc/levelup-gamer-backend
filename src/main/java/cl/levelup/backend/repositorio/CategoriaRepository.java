package cl.levelup.backend.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.levelup.backend.entidad.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
}
