package co.plany.plany.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate; // Importa LocalDate para manejar fechas

@Entity
@Table(name = "recordatorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recordatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_recor")
    private Integer codRecor;

    @Column(name = "mensaje", length = 30)
    private String mensaje;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDate fechaHora; // Usa LocalDate para la fecha sin hora, si la base de datos es solo date
                                 // Si la columna es 'timestamp', usa LocalDateTime
}