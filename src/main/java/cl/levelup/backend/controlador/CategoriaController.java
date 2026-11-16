package cl.levelup.backend.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.levelup.backend.dto.CategoriaRequest;
import cl.levelup.backend.dto.CategoriaResponse;
import cl.levelup.backend.servicio.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Lectura pública, escritura solo ADMIN")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar categorías")
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una categoría")
    @ApiResponse(responseCode = "404", description = "No existe")
    public CategoriaResponse obtener(@PathVariable Integer id) {
        return categoriaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear categoría (ADMIN)")
    @ApiResponse(responseCode = "409", description = "Nombre repetido")
    public CategoriaResponse crear(@Valid @RequestBody CategoriaRequest datos) {
        return categoriaService.crear(datos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar categoría (ADMIN)")
    public CategoriaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaRequest datos) {
        return categoriaService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar categoría (ADMIN)")
    @ApiResponse(responseCode = "409", description = "La categoría tiene productos")
    public void eliminar(@PathVariable Integer id) {
        categoriaService.eliminar(id);
    }
}
