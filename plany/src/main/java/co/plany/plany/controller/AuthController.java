package co.plany.plany.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.plany.plany.model.Usuario;
import co.plany.plany.service.UsuarioService;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * @brief Endpoint para registrar un nuevo usuario.
     * Recibe un objeto Usuario (con nombre, correo y contraseña) y lo guarda en la BD.
     * @param usuario El objeto Usuario a registrar.
     * @return ResponseEntity con el usuario registrado o un mensaje de error.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsu(registerRequest.getName());
            nuevoUsuario.setCorreoUsu(registerRequest.getEmail());
            nuevoUsuario.setContrasena(registerRequest.getPassword());
            
            Usuario usuarioGuardado = usuarioService.registrarUsuario(nuevoUsuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("userId", usuarioGuardado.getIdUsuario());
            response.put("userName", usuarioGuardado.getNombreUsu());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * @brief Endpoint para iniciar sesión.
     * Recibe el correo y la contraseña, verifica las credenciales.
     * @param credentials Un mapa que contiene "correoUsu" y "contrasena".
     * @return ResponseEntity con el ID del usuario si el inicio de sesión es exitoso, o un mensaje de error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<Usuario> usuarioOptional = usuarioService.iniciarSesion(loginRequest.getEmail(), loginRequest.getPassword());
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("userId", usuario.getIdUsuario());
                response.put("userName", usuario.getNombreUsu());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Credenciales inválidas");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al iniciar sesión: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Plany API is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    // Clases internas para las requests
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
