package co.plany.plany.model;

import jakarta.persistence.Column; // Importa todas las anotaciones de JPA necesarias
import jakarta.persistence.Entity; // Importa Lombok para generar getters, setters, etc.
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Indica que esta clase es una entidad JPA y se mapea a una tabla de BD
@Table(name = "usuario") // Especifica el nombre de la tabla en la base de datos (minúsculas por convención en PostgreSQL)
@Data // Anotación de Lombok para generar automáticamente getters, setters, toString(), equals(), hashCode()
@NoArgsConstructor // Anotación de Lombok para generar un constructor sin argumentos
@AllArgsConstructor // Anotación de Lombok para generar un constructor con todos los argumentos
public class Usuario {

    @Id // Marca el campo como la clave primaria de la entidad
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indica que la BD generará el valor del ID (serial en PostgreSQL)
    @Column(name = "id_usuario") // Mapea el campo a la columna 'id_usuario' en la tabla 'usuario'
    private Integer idUsuario; // Tipo Integer para IDs seriales

    @Column(name = "nombre_usu", nullable = false, length = 30) // Mapea a 'nombre_usu', no nulo, longitud máxima 30
    private String nombreUsu;

    @Column(name = "correo_usu", nullable = false, length = 35, unique = true) // Mapea a 'correo_usu', no nulo, longitud máxima 35. Añadido 'unique = true' para asegurar correos únicos.
    private String correoUsu;

    @Column(name = "contrasena", nullable = false, length = 100) // Nuevo campo para la contraseña, no nulo.
                                                                 // Se usa una longitud mayor para almacenar contraseñas hasheadas.
    private String contrasena;

    // Nota: Aunque el script tiene un trigger para Log_Tarea_Creada,
    // la tabla Log_Tarea_Creada en sí no necesita una entidad si solo se usa para auditoría
    // y no se va a consultar directamente o modificar desde la aplicación.
    // Si necesitas interactuar con Log_Tarea_Creada desde Spring Boot (ej. para ver logs),
    // también tendrías que crear su entidad. Por ahora, nos centramos en las tablas de datos primarias.
}
