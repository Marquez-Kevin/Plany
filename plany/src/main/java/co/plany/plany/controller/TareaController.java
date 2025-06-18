package co.plany.plany.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.plany.plany.model.Tarea;
import co.plany.plany.service.TareaService;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/tareas") // Ruta base para los endpoints de tareas
public class TareaController {

    @Autowired
    private TareaService tareaService; // Inyecta el servicio de tareas

    /**
     * @brief Obtiene todas las tareas (solo para usuarios autenticados).
     *
     * @return Una lista de todas las tareas.
     */
    @GetMapping
    public ResponseEntity<List<Tarea>> getAllTareas() {
        List<Tarea> tareas = tareaService.getAllTareas();
        return new ResponseEntity<>(tareas, HttpStatus.OK);
    }

    /**
     * @brief Obtiene una tarea por su ID (solo para usuarios autenticados).
     *
     * @param id El ID de la tarea.
     * @return ResponseEntity con la tarea o un estado 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getTareaById(@PathVariable Integer id) {
        Optional<Tarea> tarea = tareaService.getTareaById(id);
        return tarea.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * @brief Crea una nueva tarea (solo para usuarios autenticados).
     *
     * @param tarea El objeto Tarea enviado en el cuerpo de la solicitud (JSON).
     * @return ResponseEntity con la tarea creada.
     */
    @PostMapping
    public ResponseEntity<Tarea> crearTarea(@RequestBody Tarea tarea) {
        Tarea nuevaTarea = tareaService.crearTarea(tarea);
        return new ResponseEntity<>(nuevaTarea, HttpStatus.CREATED);
    }

    /**
     * @brief Actualiza una tarea existente (solo para usuarios autenticados).
     *
     * @param id El ID de la tarea a actualizar.
     * @param tareaDetails Los detalles de la tarea con las actualizaciones.
     * @return ResponseEntity con la tarea actualizada o un estado 404 si no se encuentra.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizarTarea(@PathVariable Integer id, @RequestBody Tarea tareaDetails) {
        Tarea tareaActualizada = tareaService.actualizarTarea(id, tareaDetails);
        return tareaActualizada != null ?
               new ResponseEntity<>(tareaActualizada, HttpStatus.OK) :
               new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * @brief Elimina una tarea por su ID (solo para usuarios autenticados).
     *
     * @param id El ID de la tarea a eliminar.
     * @return ResponseEntity con un estado 204 (No Content) si se elimina con éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable Integer id) {
        tareaService.eliminarTarea(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * @brief Obtiene las tareas "de hoy" para un usuario específico.
     *
     * Esta ruta está permitida para todos, ya que el frontend la usa para la vista inicial.
     * Sin embargo, el frontend DEBERÍA enviar el ID del usuario logueado.
     *
     * @param userId El ID del usuario.
     * @return Una lista de tareas para hoy.
     */
    @GetMapping("/today/{userId}")
    public ResponseEntity<List<Tarea>> getTareasDeHoy(@PathVariable Integer userId) {
        List<Tarea> tareas = tareaService.getTareasDeHoy(userId);
        return new ResponseEntity<>(tareas, HttpStatus.OK);
    }

    /**
     * @brief Marca una tarea como completada (solo para usuarios autenticados).
     *
     * @param taskId El ID de la tarea a marcar como completada.
     * @return ResponseEntity con la tarea actualizada o un estado 404 si no se encuentra.
     */
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Tarea> marcarTareaComoCompletada(@PathVariable Integer taskId) {
        Tarea tareaCompletada = tareaService.marcarTareaComoCompletada(taskId);
        return tareaCompletada != null ?
               new ResponseEntity<>(tareaCompletada, HttpStatus.OK) :
               new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * @brief Calcula el progreso semanal de un usuario.
     *
     * Esta ruta está permitida para todos, similar a las tareas de hoy.
     *
     * @param userId El ID del usuario.
     * @return El porcentaje de progreso semanal.
     */
    @GetMapping("/progress/{userId}")
    public ResponseEntity<Double> getProgresoSemanal(@PathVariable Integer userId) {
        double progreso = tareaService.getProgresoSemanal(userId);
        return new ResponseEntity<>(progreso, HttpStatus.OK);
    }
}
