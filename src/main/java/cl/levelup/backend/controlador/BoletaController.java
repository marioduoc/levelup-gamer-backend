package cl.levelup.backend.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.levelup.backend.dto.BoletaResponse;
import cl.levelup.backend.dto.CompraRequest;
import cl.levelup.backend.dto.EstadoBoletaRequest;
import cl.levelup.backend.seguridad.UsuarioAutenticado;
import cl.levelup.backend.servicio.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas", description = "Compras y órdenes")
public class BoletaController {

    private final BoletaService boletaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @Operation(summary = "Comprar (CLIENTE, ADMIN)",
            description = "Valida stock, calcula precios en el servidor y descuenta stock si el pago se aprueba. "
                    + "Tarjeta terminada en 0 = pago rechazado.")
    @ApiResponse(responseCode = "201", description = "Boleta creada (APROBADA o RECHAZADA)")
    @ApiResponse(responseCode = "404", description = "Producto inexistente")
    @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    public BoletaResponse comprar(@Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual,
            @Valid @RequestBody CompraRequest compra) {
        return boletaService.comprar(actual.id(), compra);
    }

    @GetMapping("/mias")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mis compras")
    public List<BoletaResponse> misBoletas(@Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual) {
        return boletaService.listarDeUsuario(actual.id());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(summary = "Listar todas las órdenes (ADMIN, VENDEDOR)")
    public List<BoletaResponse> listar() {
        return boletaService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Detalle de una orden", description = "ADMIN y VENDEDOR ven todas; CLIENTE solo las suyas.")
    @ApiResponse(responseCode = "403", description = "La boleta es de otro cliente")
    @ApiResponse(responseCode = "404", description = "No existe")
    public BoletaResponse obtener(@PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual) {
        return boletaService.obtener(id, actual.id(), actual.esAdminOVendedor());
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Anular una orden (ADMIN)", description = "Solo APROBADA -> ANULADA; devuelve el stock.")
    @ApiResponse(responseCode = "409", description = "La boleta no está aprobada")
    public BoletaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoBoletaRequest datos) {
        return boletaService.cambiarEstado(id, datos.estado());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una orden rechazada o anulada (ADMIN)")
    @ApiResponse(responseCode = "409", description = "Una boleta aprobada no se elimina")
    public void eliminar(@PathVariable Long id) {
        boletaService.eliminar(id);
    }
}
