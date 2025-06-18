package co.plany.plany.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.plany.plany.model.Usuario;
import co.plany.plany.service.UsuarioService;

@RestController
@RequestMapping("http://localhost:8081/api/auth")
// ¡CRÍTICO! Cambiado a *solo* el origen de Spring Boot.
// Si el frontend está en src/main/resources/static, este es el único origen necesario.
@CrossOrigin(origins = "http://localhost:8081") 
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * @brief Endpoint para registrar un nuevo usuario.
     * Recibe un objeto Usuario (con nombre, correo y contraseña) y lo guarda en la BD.
     * @param usuario El objeto Usuario a registrar.
     * @return ResponseEntity con el usuario registrado o un mensaje de error.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Usuario usuario) {
        System.out.println("Backend: Solicitud de registro recibida para correo: " + usuario.getCorreoUsu()); // Log de entrada
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
            System.out.println("Backend: Usuario registrado exitosamente con ID: " + nuevoUsuario.getIdUsuario()); // Log de éxito
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Usuario registrado exitosamente con ID: " + nuevoUsuario.getIdUsuario()),
                HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            System.err.println("Backend: Error al registrar usuario: " + e.getMessage()); // Log de error
            return new ResponseEntity<>(
                Collections.singletonMap("message", e.getMessage()),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * @brief Endpoint para iniciar sesión.
     * Recibe el correo y la contraseña, verifica las credenciales.
     * @param credentials Un mapa que contiene "correoUsu" y "contrasena".
     * @return ResponseEntity con el ID del usuario si el inicio de sesión es exitoso, o un mensaje de error.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> credentials) {
        String correoUsu = credentials.get("correoUsu");
        String contrasena = credentials.get("contrasena");

        System.out.println("Backend: Solicitud de inicio de sesión recibida para correo: " + correoUsu); // Log de entrada

        if (correoUsu == null || contrasena == null) {
            System.err.println("Backend: Correo o contraseña faltantes en la solicitud de login."); // Log de error
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Correo y contraseña son requeridos."),
                HttpStatus.BAD_REQUEST
            );
        }

        Optional<Usuario> usuarioOptional = usuarioService.iniciarSesion(correoUsu, contrasena);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            System.out.println("Backend: Inicio de sesión exitoso para usuario ID: " + usuario.getIdUsuario()); // Log de éxito
            return new ResponseEntity<>(
                Map.of("message", "Inicio de sesión exitoso", "userId", usuario.getIdUsuario()),
                HttpStatus.OK
            );
        } else {
            System.err.println("Backend: Credenciales de inicio de sesión inválidas para correo: " + correoUsu); // Log de error
            return new ResponseEntity<>(
                Collections.singletonMap("message", "Credenciales inválidas."),
                HttpStatus.UNAUTHORIZED
            );
        }
    }
}
