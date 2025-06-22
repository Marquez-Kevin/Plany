package co.plany.plany.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate; // Importa LocalDate para manejar fechas

@Entity
@Table(name = "tarea")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarea")
    private Integer idTarea;

    @Column(name = "titulo", nullable = false, length = 30)
    private String titulo;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion; // Usa LocalDate para la fecha

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin; // Usa LocalDate para la fecha

    // Relaciones @ManyToOne para las claves foráneas
    // Cada @JoinColumn especifica la columna en la tabla 'tarea' que es la FK
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading: carga la entidad Recordatorio solo cuando se necesita
    @JoinColumn(name = "cod_recor") // Nombre de la columna FK en la tabla 'tarea'
    private Recordatorio recordatorio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_tipo")
    private Tipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_prio")
    private Prioridad prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_cat")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_est")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) // id_usuario no puede ser nulo
    private Usuario usuario;
}
