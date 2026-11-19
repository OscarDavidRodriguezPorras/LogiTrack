package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.request.UsuarioRequest;
import com.s2.Logitrack.dto.response.UsuarioResponse;
import com.s2.Logitrack.exception.SolicitudIncorrectaException;
import com.s2.Logitrack.exception.ProductoNoEncontradoException;
import com.s2.Logitrack.exception.SolicitudIncorrectaException;
import com.s2.Logitrack.model.Rol;
import com.s2.Logitrack.model.Usuario;
import com.s2.Logitrack.repository.RolRepository;
import com.s2.Logitrack.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new SolicitudIncorrectaException("El username '" + request.username() + "' ya está en uso");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(buscarRol(request.rol()));
        usuario.setActivo(true);

        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, String nuevoRol, String usernameActual) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Usuario", id));

        if (usuario.getUsername().equals(usernameActual)) {
            throw new SolicitudIncorrectaException("No puedes cambiar tu propio rol");
        }

        usuario.setRol(buscarRol(nuevoRol));
        return toResponse(usuarioRepository.save(usuario));
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre.trim().toUpperCase())
                .orElseThrow(() -> new SolicitudIncorrectaException(
                        "El rol '" + nombre + "' no existe. Roles válidos: ADMIN, EMPLEADO"));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getUsername(),
                usuario.getRol().getNombre(),
                usuario.getActivo());
    }
}
