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

import cl.levelup.backend.dto.PerfilRequest;
import cl.levelup.backend.dto.UsuarioRequest;
import cl.levelup.backend.dto.UsuarioResponse;
import cl.levelup.backend.seguridad.UsuarioAutenticado;
import cl.levelup.backend.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Perfil propio para cualquier usuario logueado; el resto solo ADMIN")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Ver mi perfil")
    public UsuarioResponse miPerfil(@Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual) {
        return usuarioService.obtener(actual.id());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar mi perfil", description = "No permite cambiar correo, RUN ni rol.")
    public UsuarioResponse actualizarPerfil(@Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual,
            @Valid @RequestBody PerfilRequest datos) {
        return usuarioService.actualizarPerfil(actual.id(), datos);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios (ADMIN)")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario (ADMIN)")
    @ApiResponse(responseCode = "404", description = "No existe")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear usuario con cualquier rol (ADMIN)")
    @ApiResponse(responseCode = "409", description = "RUN o correo repetido")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest datos) {
        return usuarioService.crear(datos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar usuario (ADMIN)", description = "Si password viene vacío se mantiene la actual.")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest datos,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual) {
        return usuarioService.actualizar(id, datos, actual.id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario (ADMIN)")
    @ApiResponse(responseCode = "409", description = "Es la propia cuenta o tiene compras")
    public void eliminar(@PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado actual) {
        usuarioService.eliminar(id, actual.id());
    }
}
