package cl.levelup.backend.seguridad;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.levelup.backend.entidad.Rol;
import cl.levelup.backend.entidad.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";

    private final SecretKey clave;
    private final Duration duracionAccess;
    private final Duration duracionRefresh;

    public JwtService(
            @Value("${jwt.secret}") String secreto,
            @Value("${jwt.access-minutos}") long accessMinutos,
            @Value("${jwt.refresh-dias}") long refreshDias) {
        // HS256 exige una clave de al menos 256 bits (32 bytes)
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionAccess = Duration.ofMinutes(accessMinutos);
        this.duracionRefresh = Duration.ofDays(refreshDias);
    }

    public String generarAccessToken(Usuario usuario) {
        return generar(usuario, TIPO_ACCESS, duracionAccess);
    }

    public String generarRefreshToken(Usuario usuario) {
        return generar(usuario, TIPO_REFRESH, duracionRefresh);
    }

    public long segundosAccess() {
        return duracionAccess.toSeconds();
    }

    // verifica firma, expiración y tipo; si algo falla lanza JwtException
    public UsuarioAutenticado validar(String token, String tipoEsperado) {
        Claims claims = Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!tipoEsperado.equals(claims.get("tipo", String.class))) {
            throw new JwtException("Tipo de token incorrecto.");
        }
        return new UsuarioAutenticado(
                Long.valueOf(claims.getSubject()),
                claims.get("correo", String.class),
                Rol.valueOf(claims.get("rol", String.class)));
    }

    private String generar(Usuario usuario, String tipo, Duration duracion) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("correo", usuario.getCorreo())
                .claim("rol", usuario.getRol().name())
                .claim("tipo", tipo)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(duracion)))
                .signWith(clave)
                .compact();
    }
}
