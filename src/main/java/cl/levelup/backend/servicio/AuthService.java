package cl.levelup.backend.servicio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.levelup.backend.dto.LoginRequest;
import cl.levelup.backend.dto.TokenResponse;
import cl.levelup.backend.dto.UsuarioResponse;
import cl.levelup.backend.entidad.Usuario;
import cl.levelup.backend.excepcion.NoAutenticadoException;
import cl.levelup.backend.repositorio.UsuarioRepository;
import cl.levelup.backend.seguridad.JwtService;
import cl.levelup.backend.seguridad.UsuarioAutenticado;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(datos.correo().trim())
                .filter(u -> passwordEncoder.matches(datos.password(), u.getPassword()))
                // mismo mensaje si falla el correo o la clave, para no revelar qué cuentas existen
                .orElseThrow(() -> new NoAutenticadoException("Correo o contraseña incorrectos."));
        verificarActivo(usuario);
        return emitirTokens(usuario);
    }

    // entrega un par de tokens nuevo a partir de un refresh token vigente
    @Transactional(readOnly = true)
    public TokenResponse renovar(String refreshToken) {
        UsuarioAutenticado datos;
        try {
            datos = jwtService.validar(refreshToken, JwtService.TIPO_REFRESH);
        } catch (JwtException | IllegalArgumentException e) {
            throw new NoAutenticadoException("El refresh token no es válido o expiró.");
        }
        Usuario usuario = usuarioRepository.findById(datos.id())
                .orElseThrow(() -> new NoAutenticadoException("El usuario ya no existe."));
        verificarActivo(usuario);
        return emitirTokens(usuario);
    }

    private void verificarActivo(Usuario usuario) {
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new NoAutenticadoException("La cuenta está desactivada.");
        }
    }

    private TokenResponse emitirTokens(Usuario usuario) {
        return new TokenResponse(
                jwtService.generarAccessToken(usuario),
                jwtService.generarRefreshToken(usuario),
                "Bearer",
                jwtService.segundosAccess(),
                UsuarioResponse.de(usuario));
    }
}
