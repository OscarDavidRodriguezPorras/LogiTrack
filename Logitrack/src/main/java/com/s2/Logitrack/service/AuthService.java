package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.request.LoginRequest;
import com.s2.Logitrack.dto.request.RegisterRequest;
import com.s2.Logitrack.dto.response.AuthResponse;
import com.s2.Logitrack.exception.SolicitudIncorrectaException;
import com.s2.Logitrack.exception.ProductoNoEncontradoException;
import com.s2.Logitrack.model.Rol;
import com.s2.Logitrack.model.Usuario;
import com.s2.Logitrack.repository.RolRepository;
import com.s2.Logitrack.repository.UsuarioRepository;
import com.s2.Logitrack.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROL_POR_DEFECTO = "EMPLEADO";

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ProductoNoEncontradoException("Usuario '" + request.username() + "' no encontrado"));

        return construirRespuesta(usuario);
    }


    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new SolicitudIncorrectaException("El username '" + request.username() + "' ya está en uso");
        }

        Rol rol = rolRepository.findByNombre(ROL_POR_DEFECTO)
                .orElseThrow(() -> new ProductoNoEncontradoException("Rol " + ROL_POR_DEFECTO
                        + " no existe; ejecuta data.sql"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setUsername(request.username());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        return construirRespuesta(usuarioRepository.save(usuario));
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        String rol = usuario.getRol().getNombre();
        String token = jwtService.generateToken(usuario.getUsername(), rol);
        return new AuthResponse(token, "Bearer", usuario.getUsername(), rol);
    }
}