package cl.levelup.backend.servicio;

import java.util.List;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.levelup.backend.dto.PerfilRequest;
import cl.levelup.backend.dto.RegistroRequest;
import cl.levelup.backend.dto.UsuarioRequest;
import cl.levelup.backend.dto.UsuarioResponse;
import cl.levelup.backend.entidad.Rol;
import cl.levelup.backend.entidad.Usuario;
import cl.levelup.backend.excepcion.ConflictoException;
import cl.levelup.backend.excepcion.NoEncontradoException;
import cl.levelup.backend.excepcion.SolicitudInvalidaException;
import cl.levelup.backend.repositorio.BoletaRepository;
import cl.levelup.backend.repositorio.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BoletaRepository boletaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.de(buscar(id));
    }

    // registro público: siempre queda como CLIENTE
    @Transactional
    public UsuarioResponse registrar(RegistroRequest datos) {
        validarUnicos(datos.run(), datos.correo(), null);
        Usuario usuario = new Usuario();
        usuario.setRun(datos.run().trim().toUpperCase());
        usuario.setCorreo(datos.correo().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(datos.password()));
        usuario.setRol(Rol.CLIENTE);
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(datos.apellidos().trim());
        usuario.setFechaNacimiento(datos.fechaNacimiento());
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setDireccion(datos.direccion().trim());
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest datos) {
        if (datos.password() == null || datos.password().isBlank()) {
            throw new SolicitudInvalidaException("La contraseña es requerida para crear un usuario.");
        }
        validarUnicos(datos.run(), datos.correo(), null);
        Usuario usuario = new Usuario();
        copiarDatos(datos, usuario);
        usuario.setPassword(passwordEncoder.encode(datos.password()));
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest datos, Long idAdminActual) {
        Usuario usuario = buscar(id);
        boolean seQuitaAdmin = datos.rol() != Rol.ADMIN || Boolean.FALSE.equals(datos.activo());
        if (id.equals(idAdminActual) && seQuitaAdmin) {
            throw new ConflictoException("No puedes quitarte el rol de administrador ni desactivar tu propia cuenta.");
        }
        validarUnicos(datos.run(), datos.correo(), id);
        copiarDatos(datos, usuario);
        if (datos.password() != null && !datos.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.password()));
        }
        return UsuarioResponse.de(usuario);
    }

    @Transactional
    public void eliminar(Long id, Long idAdminActual) {
        Usuario usuario = buscar(id);
        if (id.equals(idAdminActual)) {
            throw new ConflictoException("No puedes eliminar tu propia cuenta.");
        }
        if (boletaRepository.existsByUsuarioId(id)) {
            throw new ConflictoException("El usuario tiene compras registradas. Desactívalo en vez de eliminarlo.");
        }
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(Long id, PerfilRequest datos) {
        Usuario usuario = buscar(id);
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(datos.apellidos().trim());
        usuario.setFechaNacimiento(datos.fechaNacimiento());
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setDireccion(datos.direccion());
        if (datos.password() != null && !datos.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datos.password()));
        }
        return UsuarioResponse.de(usuario);
    }

    Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el usuario " + id + "."));
    }

    private void copiarDatos(UsuarioRequest datos, Usuario usuario) {
        usuario.setRun(datos.run().trim().toUpperCase());
        usuario.setCorreo(datos.correo().trim().toLowerCase());
        usuario.setRol(datos.rol());
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(datos.apellidos().trim());
        usuario.setFechaNacimiento(datos.fechaNacimiento());
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setDireccion(datos.direccion());
        usuario.setActivo(datos.activo() == null || datos.activo());
    }

    private void validarUnicos(String run, String correo, Long idActual) {
        usuarioRepository.findByRun(run.trim().toUpperCase())
                .filter(u -> !Objects.equals(u.getId(), idActual))
                .ifPresent(u -> {
                    throw new ConflictoException("Ya existe un usuario registrado con ese RUN.");
                });
        usuarioRepository.findByCorreoIgnoreCase(correo.trim())
                .filter(u -> !Objects.equals(u.getId(), idActual))
                .ifPresent(u -> {
                    throw new ConflictoException("Ya existe un usuario registrado con ese correo.");
                });
    }
}
