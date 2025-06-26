package co.plany.plany.repository;

import co.plany.plany.model.Tarea; // Importa la entidad Tarea
import co.plany.plany.model.Usuario; // Importa la entidad Usuario
import co.plany.plany.model.Estado; // Importa la entidad Estado

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {
    // Método para encontrar tareas por un usuario y por un estado específico
    List<Tarea> findByUsuarioAndEstado(Usuario usuario, Estado estado);

    // Método para encontrar todas las tareas de un usuario
    List<Tarea> findByUsuario(Usuario usuario);

    /**
     * @brief Encuentra tareas para un usuario y un estado específicos,
     * cuya fecha de fin es menor o igual a la fecha proporcionada.
     * Útil para obtener "tareas de hoy" que incluyen pendientes pasadas.
     * @param usuario El objeto Usuario al que pertenecen las tareas.
     * @param estado El objeto Estado de las tareas (ej. "Pendiente").
     * @param fechaFin La fecha límite (ej. LocalDate.now()).
     * @return Una lista de tareas que cumplen los criterios.
     */
    List<Tarea> findByUsuarioAndEstadoAndFechaFinLessThanEqual(Usuario usuario, Estado estado, LocalDate fechaFin);

    /**
     * @brief Encuentra tareas para un usuario con fecha de fin entre dos fechas dadas.
     * Útil para calcular el progreso semanal.
     * @param usuario El objeto Usuario al que pertenecen las tareas.
     * @param startDate La fecha de inicio del rango (inclusive).
     * @param endDate La fecha de fin del rango (inclusive).
     * @return Una lista de tareas que cumplen los criterios.
     */
    List<Tarea> findByUsuarioAndFechaFinBetween(Usuario usuario, LocalDate startDate, LocalDate endDate);

    // Puedes añadir más métodos personalizados aquí según tus necesidades


     // Nuevo método para encontrar tareas después de una fecha específica
    List<Tarea> findByUsuarioAndFechaCreacionAfter(Usuario usuario, LocalDate fecha);
}