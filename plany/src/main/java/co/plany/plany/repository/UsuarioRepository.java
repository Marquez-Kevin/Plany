package co.plany.plany.repository;

import co.plany.plany.model.Usuario; // Importa la entidad Usuario
import org.springframework.data.jpa.repository.JpaRepository; // Importa JpaRepository
import org.springframework.stereotype.Repository; // Importa la anotación Repository

import java.util.Optional; // Importa Optional para manejar el caso de no encontrar un usuario

@Repository // Indica que esta interfaz es un componente de Spring para acceso a datos
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // JpaRepository<Entidad, Tipo_del_ID> proporciona métodos CRUD básicos (save, findById, findAll, delete, etc.)

    // Método personalizado para encontrar un usuario por su correo electrónico
    // Spring Data JPA puede inferir la consulta a partir del nombre del método
    Optional<Usuario> findByCorreoUsu(String correoUsu);
}