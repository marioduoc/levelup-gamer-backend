package cl.levelup.backend.seguridad;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

// respuestas JSON para 401 (sin sesión) y 403 (sin permisos) que corta Spring Security
@Component
public class RespuestasSeguridad {

    public AuthenticationEntryPoint noAutenticado() {
        return (request, response, e) -> {
            Object detalle = request.getAttribute(JwtFiltro.ATRIBUTO_ERROR);
            String mensaje = detalle != null ? detalle.toString() : "Debes iniciar sesión.";
            escribir(response, 401, "Unauthorized", mensaje);
        };
    }

    public AccessDeniedHandler sinPermisos() {
        return (request, response, e) -> escribir(response, 403, "Forbidden",
                "No tienes permisos para realizar esta acción.");
    }

    private void escribir(HttpServletResponse response, int status, String error, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"status":%d,"error":"%s","mensaje":"%s","fecha":"%s"}"""
                .formatted(status, error, mensaje, LocalDateTime.now()));
    }
}
