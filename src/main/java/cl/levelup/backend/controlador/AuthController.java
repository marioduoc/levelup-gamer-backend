package cl.levelup.backend.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.levelup.backend.dto.LoginRequest;
import cl.levelup.backend.dto.RefreshRequest;
import cl.levelup.backend.dto.RegistroRequest;
import cl.levelup.backend.dto.TokenResponse;
import cl.levelup.backend.dto.UsuarioResponse;
import cl.levelup.backend.servicio.AuthService;
import cl.levelup.backend.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, login y renovación de tokens (público)")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un cliente nuevo")
    @ApiResponse(responseCode = "201", description = "Usuario creado con rol CLIENTE")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "RUN o correo ya registrados")
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest datos) {
        return usuarioService.registrar(datos);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un access token (30 min) y un refresh token (7 días).")
    @ApiResponse(responseCode = "200", description = "Login correcto")
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta desactivada")
    public TokenResponse login(@Valid @RequestBody LoginRequest datos) {
        return authService.login(datos);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar tokens", description = "Recibe el refresh token y entrega un par nuevo.")
    @ApiResponse(responseCode = "200", description = "Tokens renovados")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    public TokenResponse renovar(@Valid @RequestBody RefreshRequest datos) {
        return authService.renovar(datos.refreshToken());
    }
}
