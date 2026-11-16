package cl.levelup.backend.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tipo,
        long expiraEnSegundos,
        UsuarioResponse usuario) {
}
