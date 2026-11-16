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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.levelup.backend.dto.ProductoRequest;
import cl.levelup.backend.dto.ProductoResponse;
import cl.levelup.backend.servicio.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo: lectura pública, escritura solo ADMIN")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Filtros opcionales por categoría o solo ofertas.")
    public List<ProductoResponse> listar(
            @Parameter(description = "Id de categoría") @RequestParam(required = false) Integer categoriaId,
            @Parameter(description = "true para ver solo productos en oferta") @RequestParam(defaultValue = "false") boolean ofertas) {
        return productoService.listar(categoriaId, ofertas);
    }

    @GetMapping("/stock-critico")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Productos con stock crítico (ADMIN)")
    public List<ProductoResponse> stockCritico() {
        return productoService.stockCritico();
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener un producto por código")
    @ApiResponse(responseCode = "404", description = "No existe")
    public ProductoResponse obtener(@PathVariable String codigo) {
        return productoService.obtener(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "Código repetido")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest datos) {
        return productoService.crear(datos);
    }

    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto (ADMIN)", description = "El código no se modifica.")
    @ApiResponse(responseCode = "404", description = "No existe")
    public ProductoResponse actualizar(@PathVariable String codigo, @Valid @RequestBody ProductoRequest datos) {
        return productoService.actualizar(codigo, datos);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto (ADMIN)")
    @ApiResponse(responseCode = "409", description = "El producto tiene ventas registradas")
    public void eliminar(@PathVariable String codigo) {
        productoService.eliminar(codigo);
    }
}
