package cl.levelup.backend.excepcion;

public class NoAutenticadoException extends RuntimeException {

    public NoAutenticadoException(String mensaje) {
        super(mensaje);
    }
}
