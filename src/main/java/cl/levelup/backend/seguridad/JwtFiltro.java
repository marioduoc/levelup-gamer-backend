package cl.levelup.backend.seguridad;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// lee el header Authorization: Bearer <token> en cada petición
@Component
@RequiredArgsConstructor
public class JwtFiltro extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR = "jwt_error";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                UsuarioAutenticado usuario = jwtService.validar(header.substring(7), JwtService.TIPO_ACCESS);
                var permisos = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().name()));
                var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, permisos);
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (ExpiredJwtException e) {
                request.setAttribute(ATRIBUTO_ERROR, "El token expiró.");
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ATRIBUTO_ERROR, "El token no es válido.");
            }
        }
        chain.doFilter(request, response);
    }
}
