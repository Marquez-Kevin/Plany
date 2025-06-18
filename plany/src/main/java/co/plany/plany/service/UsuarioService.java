package co.plany.plany.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; // Inyecta la interfaz

import co.plany.plany.model.Usuario;
import co.plany.plany.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder

    public Usuario registrarUsuario(Usuario usuario) {
        System.out.println("Intentando registrar usuario en servicio: " + usuario.getCorreoUsu()); // Log de servicio
        // Verifica si ya existe un usuario con el mismo correo
        if (usuarioRepository.findByCorreoUsu(usuario.getCorreoUsu()).isPresent()) {
            System.err.println("Error: Ya existe un usuario con este correo electrónico: " + usuario.getCorreoUsu()); // Log de error
            throw new RuntimeException("Ya existe un usuario con este correo electrónico.");
        }
        // Hashea la contraseña antes de guardarla
        String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);
        System.out.println("Contraseña hasheada y lista para guardar para usuario: " + usuario.getCorreoUsu()); // Log de hasheo
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> iniciarSesion(String correoUsu, String contrasena) {
        System.out.println("Intentando iniciar sesión en servicio para correo: " + correoUsu); // Log de servicio
        // Busca el usuario por correo electrónico
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCorreoUsu(correoUsu);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Compara la contraseña proporcionada con la contraseña hasheada
            if (passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                System.out.println("Credenciales válidas para correo: " + correoUsu); // Log de éxito
                return Optional.of(usuario); // Credenciales válidas
            } else {
                System.err.println("Contraseña inválida para correo: " + correoUsu); // Log de error
            }
        } else {
            System.err.println("Usuario no encontrado para correo: " + correoUsu); // Log de error
        }
        return Optional.empty(); // Usuario no encontrado o credenciales inválidas
    }

    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByCorreoUsu(String correoUsu) {
        return usuarioRepository.findByCorreoUsu(correoUsu);
    }
}