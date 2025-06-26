package co.plany.plany.controller;


import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.plany.plany.model.Tarea;
import co.plany.plany.model.Estado;
import co.plany.plany.model.Tipo;
import co.plany.plany.service.TareaService;
import co.plany.plany.repository.EstadoRepository;
import co.plany.plany.repository.TipoRepository;
import co.plany.plany.dto.SugerenciaRepetitivaDTO;
import co.plany.plany.service.AnalisisTareasService;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/tareas") // Ruta base para los endpoints de tareas
public class TareaController {

    @Autowired
    private TareaService tareaService; // Inyecta el servicio de tareas

    @Autowired
    private EstadoRepository estadoRepository; // Inyecta el repositorio de estados

    @Autowired
    private TipoRepository tipoRepository; // Inyecta el repositorio de tipos

    @Autowired
    private AnalisisTareasService analisisTareasService; // Inyecta el servicio de análisis de tareas

    

    /**
     * @brief Obtiene todos los estados disponibles.
     *
     * @return Una lista de todos los estados.
     */
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> getAllEstados() {
        List<Estado> estados = estadoRepository.findAll();
        return new ResponseEntity<>(estados, HttpStatus.OK);
    }

    /**
     * @brief Obtiene todos los tipos disponibles.
     *
     * @return Una lista de todos los tipos.
     */
    @GetMapping("/tipos")
    public ResponseEntity<List<Tipo>> getAllTipos() {
        List<Tipo> tipos = tipoRepository.findAll();
        return new ResponseEntity<>(tipos, HttpStatus.OK);
    }

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
    public ResponseEntity<Map<String, String>> eliminarTarea(@PathVariable Integer id) {
        try {
            tareaService.eliminarTarea(id);
            return new ResponseEntity<>(Map.of("message", "Tarea eliminada exitosamente"), HttpStatus.OK);
        } catch (RuntimeException e) {
            System.err.println("Error al eliminar tarea: " + e.getMessage());
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error inesperado al eliminar tarea: " + e.getMessage());
            return new ResponseEntity<>(Map.of("message", "Error interno del servidor"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     * @brief Alterna el estado de una tarea entre pendiente y completada.
     *
     * @param taskId El ID de la tarea a alternar estado.
     * @return ResponseEntity con la tarea actualizada o un estado 404 si no se encuentra.
     */
    @PutMapping("/{taskId}/toggle")
    public ResponseEntity<Tarea> alternarEstadoTarea(@PathVariable Integer taskId) {
        Tarea tareaAlternada = tareaService.alternarEstadoTarea(taskId);
        return tareaAlternada != null ?
               new ResponseEntity<>(tareaAlternada, HttpStatus.OK) :
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

    /**
     * @brief Endpoint temporal para limpiar estados duplicados.
     *
     * @return Mensaje de confirmación.
     */
    @GetMapping("/cleanup")
    public ResponseEntity<String> cleanupEstados() {
        try {
            // Obtener todos los estados
            List<Estado> todosEstados = estadoRepository.findAll();
            System.out.println("Estados encontrados: " + todosEstados.size());
            
            // Mantener solo los primeros estados únicos
            List<Estado> estadosUnicos = new ArrayList<>();
            for (Estado estado : todosEstados) {
                boolean yaExiste = estadosUnicos.stream()
                    .anyMatch(e -> e.getNombreEstado().equals(estado.getNombreEstado()));
                if (!yaExiste) {
                    estadosUnicos.add(estado);
                }
            }
            
            // Eliminar estados duplicados
            for (Estado estado : todosEstados) {
                if (!estadosUnicos.contains(estado)) {
                    estadoRepository.delete(estado);
                }
            }
            
            return new ResponseEntity<>("Estados duplicados eliminados. Estados restantes: " + estadosUnicos.size(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @brief Endpoint de debug para ver todas las tareas de un usuario.
     *
     * @param userId El ID del usuario.
     * @return Lista de todas las tareas del usuario.
     */
    @GetMapping("/debug/{userId}")
    public ResponseEntity<List<Tarea>> getTareasDebug(@PathVariable Integer userId) {
        try {
            List<Tarea> todasLasTareas = tareaService.getAllTareasByUser(userId);
            return new ResponseEntity<>(todasLasTareas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @brief Endpoint simple para probar la conexión.
     *
     * @return Lista de todas las tareas.
     */
    @GetMapping("/test")
    public ResponseEntity<List<Tarea>> testTareas() {
        try {
            List<Tarea> todasLasTareas = tareaService.getAllTareas();
            return new ResponseEntity<>(todasLasTareas, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @brief Endpoint de debug para ver todas las tareas sin filtros.
     *
     * @return Lista de todas las tareas con información detallada.
     */
    @GetMapping("/debug/all")
    public ResponseEntity<String> debugAllTareas() {
        try {
            List<Tarea> todasLasTareas = tareaService.getAllTareas();
            StringBuilder debugInfo = new StringBuilder();
            debugInfo.append("Total de tareas en BD: ").append(todasLasTareas.size()).append("\n\n");
            
            for (Tarea tarea : todasLasTareas) {
                debugInfo.append("Tarea ID: ").append(tarea.getIdTarea()).append("\n");
                debugInfo.append("  Título: ").append(tarea.getTitulo()).append("\n");
                debugInfo.append("  Usuario: ").append(tarea.getUsuario() != null ? tarea.getUsuario().getIdUsuario() : "null").append("\n");
                debugInfo.append("  Estado: ").append(tarea.getEstado() != null ? tarea.getEstado().getNombreEstado() : "null").append("\n");
                debugInfo.append("  Fecha fin: ").append(tarea.getFechaFin()).append("\n");
                debugInfo.append("  Fecha creación: ").append(tarea.getFechaCreacion()).append("\n\n");
            }
            
            return new ResponseEntity<>(debugInfo.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @brief Endpoint de debug para ver tareas de un usuario con información detallada.
     *
     * @param userId El ID del usuario.
     * @return Información detallada de las tareas del usuario.
     */
    @GetMapping("/debug/user/{userId}")
    public ResponseEntity<String> debugTareasUsuario(@PathVariable Integer userId) {
        try {
            List<Tarea> tareasUsuario = tareaService.getTareasDelUsuario(userId);
            StringBuilder debugInfo = new StringBuilder();
            debugInfo.append("Tareas del usuario ").append(userId).append(": ").append(tareasUsuario.size()).append("\n\n");
            
            for (Tarea tarea : tareasUsuario) {
                debugInfo.append("Tarea ID: ").append(tarea.getIdTarea()).append("\n");
                debugInfo.append("  Título: ").append(tarea.getTitulo()).append("\n");
                debugInfo.append("  Descripción: ").append(tarea.getDescripcion() != null ? tarea.getDescripcion() : "Sin descripción").append("\n");
                debugInfo.append("  Estado: ").append(tarea.getEstado() != null ? tarea.getEstado().getNombreEstado() : "null").append("\n");
                debugInfo.append("  Fecha de creación: ").append(tarea.getFechaCreacion()).append("\n");
                debugInfo.append("  Fecha propuesta de finalización: ").append(tarea.getFechaFin()).append("\n");
                debugInfo.append("  Prioridad: ").append(tarea.getPrioridad() != null ? tarea.getPrioridad().getNombrePrioridad() : "Sin prioridad").append("\n");
                debugInfo.append("  Categoría: ").append(tarea.getCategoria() != null ? tarea.getCategoria().getNombreCategoria() : "Sin categoría").append("\n");
                debugInfo.append("  Tipo: ").append(tarea.getTipo() != null ? tarea.getTipo().getNombreTipo() : "Sin tipo").append("\n\n");
            }
            
            return new ResponseEntity<>(debugInfo.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @brief Obtiene las tareas filtradas para un usuario específico.
     *
     * @param userId El ID del usuario.
     * @param estado Filtro por estado (opcional).
     * @param prioridad Filtro por prioridad (opcional).
     * @param categoria Filtro por categoría (opcional).
     * @param busqueda Término de búsqueda en título y descripción (opcional).
     * @param fechaInicio Filtro por fecha de inicio (opcional).
     * @param fechaFin Filtro por fecha de fin (opcional).
     * @return Lista de tareas filtradas.
     */
    @GetMapping("/filter/{userId}")
    public ResponseEntity<List<Tarea>> getTareasFiltradas(
            @PathVariable Integer userId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        
        System.out.println("=== DEBUG: getTareasFiltradas ===");
        System.out.println("Usuario ID: " + userId);
        System.out.println("Estado: " + estado);
        System.out.println("Prioridad: " + prioridad);
        System.out.println("Categoría: " + categoria);
        System.out.println("Búsqueda: " + busqueda);
        System.out.println("Fecha Inicio: " + fechaInicio);
        System.out.println("Fecha Fin: " + fechaFin);
        
        List<Tarea> tareasFiltradas = tareaService.getTareasFiltradas(userId, estado, prioridad, categoria, busqueda, fechaInicio, fechaFin);
        return new ResponseEntity<>(tareasFiltradas, HttpStatus.OK);
    }

    /**
     * @brief Obtiene sugerencias de tareas repetitivas para un usuario específico.
     *
     * @param userId El ID del usuario.
     * @return Lista de sugerencias de tareas repetitivas.
     */
    @GetMapping("/sugerencias/repetitivas/{userId}")
    public ResponseEntity<List<SugerenciaRepetitivaDTO>> getSugerenciasRepetitivas(@PathVariable Integer userId) {
        List<SugerenciaRepetitivaDTO> sugerencias = analisisTareasService.analizarTareasUsuario(userId);
        return new ResponseEntity<>(sugerencias, HttpStatus.OK);
    }
}
