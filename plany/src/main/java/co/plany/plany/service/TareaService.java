package co.plany.plany.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.plany.plany.model.Estado;
import co.plany.plany.model.Recordatorio;
import co.plany.plany.model.Tarea;
import co.plany.plany.model.Tipo;
import co.plany.plany.model.Usuario;
import co.plany.plany.repository.EstadoRepository;
import co.plany.plany.repository.RecordatorioRepository;
import co.plany.plany.repository.TareaRepository;
import co.plany.plany.repository.TipoRepository;
import co.plany.plany.repository.UsuarioRepository;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private RecordatorioRepository recordatorioRepository;

    @Autowired
    private TipoRepository tipoRepository;

    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    public List<Tarea> getAllTareasByUser(Integer userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        if (usuarioOptional.isPresent()) {
            return tareaRepository.findByUsuario(usuarioOptional.get());
        }
        return List.of();
    }

    public Optional<Tarea> getTareaById(Integer id) {
        return tareaRepository.findById(id);
    }

    public Tarea crearTarea(Tarea tarea) {
        System.out.println("=== DEBUG: crearTarea ===");
        System.out.println("Creando nueva tarea: " + tarea.getTitulo());
        System.out.println("Usuario ID: " + tarea.getUsuario().getIdUsuario());
        System.out.println("Estado: " + (tarea.getEstado() != null ? tarea.getEstado().getCodEst() : "null"));
        System.out.println("Fecha fin: " + tarea.getFechaFin());
        System.out.println("Tipo: " + (tarea.getTipo() != null ? tarea.getTipo().getCodTipo() : "null"));
        System.out.println("Prioridad: " + (tarea.getPrioridad() != null ? tarea.getPrioridad().getCodPrio() : "null"));
        System.out.println("Categoría: " + (tarea.getCategoria() != null ? tarea.getCategoria().getCodCat() : "null"));
        
        try {
            // Validar que el usuario existe
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(tarea.getUsuario().getIdUsuario());
            if (usuarioOptional.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado con ID: " + tarea.getUsuario().getIdUsuario());
            }
            
            // Validar que el estado existe si se proporciona
            if (tarea.getEstado() != null) {
                Optional<Estado> estadoOptional = estadoRepository.findById(tarea.getEstado().getCodEst());
                if (estadoOptional.isEmpty()) {
                    throw new RuntimeException("Estado no encontrado con ID: " + tarea.getEstado().getCodEst());
                }
            }
            
            // Validar que el tipo existe si se proporciona
            if (tarea.getTipo() != null) {
                Optional<Tipo> tipoOptional = tipoRepository.findById(tarea.getTipo().getCodTipo());
                if (tipoOptional.isEmpty()) {
                    throw new RuntimeException("Tipo no encontrado con ID: " + tarea.getTipo().getCodTipo());
                }
            }
            
            // Establecer fecha de creación si no existe
            if (tarea.getFechaCreacion() == null) {
                tarea.setFechaCreacion(LocalDate.now());
                System.out.println("Fecha de creación establecida: " + tarea.getFechaCreacion());
            }

            // Manejar recordatorio si existe
            if (tarea.getRecordatorio() != null && tarea.getRecordatorio().getCodRecor() == null) {
                System.out.println("Procesando recordatorio...");
                Recordatorio nuevoRecordatorio = tarea.getRecordatorio();
                Recordatorio recordatorioGuardado = recordatorioRepository.save(nuevoRecordatorio);
                tarea.setRecordatorio(recordatorioGuardado);
                System.out.println("Recordatorio guardado con ID: " + recordatorioGuardado.getCodRecor());
            }

            // Guardar la tarea
            Tarea tareaGuardada = tareaRepository.save(tarea);
            System.out.println("Tarea guardada exitosamente con ID: " + tareaGuardada.getIdTarea());
            return tareaGuardada;
            
        } catch (Exception e) {
            System.err.println("Error al crear tarea: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear la tarea: " + e.getMessage());
        }
    }

    public Tarea actualizarTarea(Integer id, Tarea tareaDetails) {
        System.out.println("=== DEBUG: actualizarTarea ===");
        System.out.println("Actualizando tarea ID: " + id);
        System.out.println("Datos recibidos: " + tareaDetails);
        
        Optional<Tarea> tareaOptional = tareaRepository.findById(id);
        if (tareaOptional.isPresent()) {
            Tarea tareaExistente = tareaOptional.get();
            System.out.println("Tarea existente encontrada: " + tareaExistente.getTitulo());
            
            // Actualizar campos básicos
            tareaExistente.setTitulo(tareaDetails.getTitulo());
            tareaExistente.setDescripcion(tareaDetails.getDescripcion());
            tareaExistente.setFechaFin(tareaDetails.getFechaFin());
            
            // Actualizar relaciones si no son null
            if (tareaDetails.getTipo() != null) {
                System.out.println("Actualizando tipo: " + tareaDetails.getTipo().getCodTipo());
                tareaExistente.setTipo(tareaDetails.getTipo());
            }
            if (tareaDetails.getPrioridad() != null) {
                System.out.println("Actualizando prioridad: " + tareaDetails.getPrioridad().getCodPrio());
                tareaExistente.setPrioridad(tareaDetails.getPrioridad());
            }
            if (tareaDetails.getCategoria() != null) {
                System.out.println("Actualizando categoría: " + tareaDetails.getCategoria().getCodCat());
                tareaExistente.setCategoria(tareaDetails.getCategoria());
            }
            if (tareaDetails.getEstado() != null) {
                System.out.println("Actualizando estado: " + tareaDetails.getEstado().getCodEst());
                tareaExistente.setEstado(tareaDetails.getEstado());
            }
            
            // Manejar recordatorio
            if (tareaDetails.getRecordatorio() != null) {
                System.out.println("Procesando recordatorio...");
                Recordatorio recordatorioNuevo = tareaDetails.getRecordatorio();
                
                if (recordatorioNuevo.getCodRecor() == null) {
                    // Es un recordatorio nuevo, guardarlo
                    System.out.println("Guardando nuevo recordatorio");
                    Recordatorio recordatorioGuardado = recordatorioRepository.save(recordatorioNuevo);
                    tareaExistente.setRecordatorio(recordatorioGuardado);
                } else {
                    // Es un recordatorio existente, actualizarlo
                    System.out.println("Actualizando recordatorio existente ID: " + recordatorioNuevo.getCodRecor());
                    Optional<Recordatorio> recordatorioExistenteOpt = recordatorioRepository.findById(recordatorioNuevo.getCodRecor());
                    if (recordatorioExistenteOpt.isPresent()) {
                        Recordatorio recordatorioExistente = recordatorioExistenteOpt.get();
                        recordatorioExistente.setMensaje(recordatorioNuevo.getMensaje());
                        recordatorioExistente.setFechaHora(recordatorioNuevo.getFechaHora());
                        Recordatorio recordatorioActualizado = recordatorioRepository.save(recordatorioExistente);
                        tareaExistente.setRecordatorio(recordatorioActualizado);
                    } else {
                        System.err.println("Recordatorio no encontrado con ID: " + recordatorioNuevo.getCodRecor());
                    }
                }
            } else {
                // Si no hay recordatorio en los datos, eliminar el existente
                System.out.println("Eliminando recordatorio existente");
                tareaExistente.setRecordatorio(null);
            }

            try {
                Tarea tareaActualizada = tareaRepository.save(tareaExistente);
                System.out.println("Tarea actualizada exitosamente: " + tareaActualizada.getIdTarea());
                return tareaActualizada;
            } catch (Exception e) {
                System.err.println("Error al guardar tarea actualizada: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al actualizar la tarea: " + e.getMessage());
            }
        } else {
            System.err.println("Tarea no encontrada con ID: " + id);
            return null;
        }
    }

    public void eliminarTarea(Integer id) {
        System.out.println("=== DEBUG: eliminarTarea ===");
        System.out.println("Eliminando tarea ID: " + id);
        
        Optional<Tarea> tareaOptional = tareaRepository.findById(id);
        if (tareaOptional.isPresent()) {
            Tarea tarea = tareaOptional.get();
            System.out.println("Tarea encontrada: " + tarea.getTitulo());
            
            try {
                // Guardar referencia al recordatorio antes de eliminar la tarea
                Recordatorio recordatorioAEliminar = tarea.getRecordatorio();
                
                // Eliminar la tarea primero
                tareaRepository.deleteById(id);
                System.out.println("Tarea eliminada exitosamente");
                
                // Ahora eliminar el recordatorio si existía
                if (recordatorioAEliminar != null) {
                    System.out.println("Eliminando recordatorio asociado ID: " + recordatorioAEliminar.getCodRecor());
                    try {
                        recordatorioRepository.deleteById(recordatorioAEliminar.getCodRecor());
                        System.out.println("Recordatorio eliminado exitosamente");
                    } catch (Exception e) {
                        System.err.println("Error al eliminar recordatorio (puede que ya no exista): " + e.getMessage());
                        // No lanzar excepción aquí, ya que la tarea ya fue eliminada exitosamente
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al eliminar tarea: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al eliminar la tarea: " + e.getMessage());
            }
        } else {
            System.err.println("Tarea no encontrada con ID: " + id);
            throw new RuntimeException("Tarea no encontrada con ID: " + id);
        }
    }

    public List<Tarea> getTareasDeHoy(Integer userId) {
        return getTareasDelUsuario(userId);
    }

    public List<Tarea> getTareasDelUsuario(Integer userId) {
        System.out.println("=== DEBUG: getTareasDelUsuario ===");
        System.out.println("Buscando tareas para usuario ID: " + userId);
        
        try {
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                System.out.println("Usuario encontrado: " + usuario.getNombreUsu() + " (ID: " + usuario.getIdUsuario() + ")");
                
                // Obtener todas las tareas del usuario
                List<Tarea> todasLasTareasDelUsuario = tareaRepository.findByUsuario(usuario);
                System.out.println("Total de tareas del usuario: " + todasLasTareasDelUsuario.size());
                
                // Ordenar por fecha de creación descendente (más recientes primero)
                List<Tarea> tareasOrdenadas = todasLasTareasDelUsuario.stream()
                    .sorted((t1, t2) -> {
                        if (t1.getFechaCreacion() == null && t2.getFechaCreacion() == null) return 0;
                        if (t1.getFechaCreacion() == null) return 1;
                        if (t2.getFechaCreacion() == null) return -1;
                        return t2.getFechaCreacion().compareTo(t1.getFechaCreacion());
                    })
                    .toList();
                
                // Mostrar información de cada tarea para debug
                for (Tarea tarea : tareasOrdenadas) {
                    System.out.println("Tarea: " + tarea.getTitulo() + 
                                     ", Estado: " + (tarea.getEstado() != null ? tarea.getEstado().getNombreEstado() : "null") +
                                     ", Fecha creación: " + tarea.getFechaCreacion() +
                                     ", Fecha fin: " + tarea.getFechaFin() +
                                     ", Usuario: " + (tarea.getUsuario() != null ? tarea.getUsuario().getIdUsuario() : "null"));
                }
                
                return tareasOrdenadas;
            } else {
                System.err.println("Usuario no encontrado con ID: " + userId);
            }
        } catch (Exception e) {
            System.err.println("Error en getTareasDelUsuario: " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
    }

    public Tarea marcarTareaComoCompletada(Integer taskId) {
        Optional<Tarea> tareaOptional = tareaRepository.findById(taskId);
        if (tareaOptional.isPresent()) {
            Tarea tarea = tareaOptional.get();
            Optional<Estado> estadoCompletadaOpt = estadoRepository.findByNombreEstado("Completada");
            if (estadoCompletadaOpt.isPresent()) {
                tarea.setEstado(estadoCompletadaOpt.get());
                return tareaRepository.save(tarea);
            } else {
                throw new RuntimeException("Estado 'Completada' no encontrado en la base de datos.");
            }
        }
        return null;
    }

    public Tarea alternarEstadoTarea(Integer taskId) {
        Optional<Tarea> tareaOptional = tareaRepository.findById(taskId);
        if (tareaOptional.isPresent()) {
            Tarea tarea = tareaOptional.get();
            
            // Obtener estados
            Optional<Estado> estadoCompletadaOpt = estadoRepository.findByNombreEstado("Completada");
            Optional<Estado> estadoPendienteOpt = estadoRepository.findByNombreEstado("Pendiente");
            
            if (estadoCompletadaOpt.isEmpty() || estadoPendienteOpt.isEmpty()) {
                throw new RuntimeException("Estados 'Completada' o 'Pendiente' no encontrados en la base de datos.");
            }
            
            Estado estadoCompletada = estadoCompletadaOpt.get();
            Estado estadoPendiente = estadoPendienteOpt.get();
            
            // Alternar estado
            if (tarea.getEstado() != null && tarea.getEstado().getNombreEstado().equals("Completada")) {
                // Si está completada, cambiar a pendiente
                tarea.setEstado(estadoPendiente);
            } else {
                // Si está pendiente o no tiene estado, cambiar a completada
                tarea.setEstado(estadoCompletada);
            }
            
            return tareaRepository.save(tarea);
        }
        return null;
    }

    public double getProgresoSemanal(Integer userId) {
        System.out.println("=== DEBUG: getProgresoSemanal ===");
        System.out.println("Calculando progreso semanal para usuario ID: " + userId);
        
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        if (usuarioOptional.isEmpty()) {
            System.err.println("Usuario no encontrado con ID: " + userId);
            return 0.0;
        }
        Usuario usuario = usuarioOptional.get();
        System.out.println("Usuario encontrado: " + usuario.getNombreUsu());

        Optional<Estado> estadoCompletadaOpt = estadoRepository.findByNombreEstado("Completada");
        if (estadoCompletadaOpt.isEmpty()) {
            System.err.println("Estado 'Completada' no encontrado en la base de datos.");
            return 0.0;
        }
        Estado estadoCompletada = estadoCompletadaOpt.get();
        System.out.println("Estado completada encontrado: " + estadoCompletada.getNombreEstado() + " (ID: " + estadoCompletada.getCodEst() + ")");

        // Obtener todas las tareas del usuario
        List<Tarea> todasLasTareasDelUsuario = tareaRepository.findByUsuario(usuario);
        System.out.println("Total de tareas del usuario: " + todasLasTareasDelUsuario.size());

        // Contar tareas completadas
        long totalTasks = todasLasTareasDelUsuario.size();
        long completedTasks = todasLasTareasDelUsuario.stream()
            .filter(tarea -> tarea.getEstado() != null && 
                           tarea.getEstado().getCodEst().equals(estadoCompletada.getCodEst()))
            .count();

        System.out.println("Tareas totales: " + totalTasks);
        System.out.println("Tareas completadas: " + completedTasks);

        if (totalTasks == 0) {
            System.out.println("No hay tareas, progreso: 0%");
            return 0.0;
        }

        double progreso = (double) completedTasks / totalTasks * 100.0;
        System.out.println("Progreso calculado: " + progreso + "%");
        return progreso;
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
    public List<Tarea> getTareasFiltradas(Integer userId, String estado, String prioridad, String categoria, String busqueda, String fechaInicio, String fechaFin) {
        System.out.println("=== DEBUG: TareaService.getTareasFiltradas ===");
        System.out.println("Usuario ID: " + userId);
        System.out.println("Estado: " + estado);
        System.out.println("Prioridad: " + prioridad);
        System.out.println("Categoría: " + categoria);
        System.out.println("Búsqueda: " + busqueda);
        System.out.println("Fecha Inicio: " + fechaInicio);
        System.out.println("Fecha Fin: " + fechaFin);
        
        // Obtener el usuario primero
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado con ID: " + userId);
            return new ArrayList<>();
        }
        
        List<Tarea> todasLasTareas = tareaRepository.findByUsuario(usuarioOpt.get());
        List<Tarea> tareasFiltradas = new ArrayList<>();
        
        for (Tarea tarea : todasLasTareas) {
            boolean cumpleFiltros = true;
            
            // Filtro por estado
            if (estado != null && !estado.trim().isEmpty()) {
                if (tarea.getEstado() == null || !tarea.getEstado().getNombreEstado().equalsIgnoreCase(estado.trim())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por prioridad
            if (cumpleFiltros && prioridad != null && !prioridad.trim().isEmpty()) {
                if (tarea.getPrioridad() == null || !tarea.getPrioridad().getNombrePrioridad().equalsIgnoreCase(prioridad.trim())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por categoría
            if (cumpleFiltros && categoria != null && !categoria.trim().isEmpty()) {
                if (tarea.getCategoria() == null || !tarea.getCategoria().getNombreCategoria().equalsIgnoreCase(categoria.trim())) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por búsqueda de texto
            if (cumpleFiltros && busqueda != null && !busqueda.trim().isEmpty()) {
                String terminoBusqueda = busqueda.trim().toLowerCase();
                String titulo = tarea.getTitulo() != null ? tarea.getTitulo().toLowerCase() : "";
                String descripcion = tarea.getDescripcion() != null ? tarea.getDescripcion().toLowerCase() : "";
                
                if (!titulo.contains(terminoBusqueda) && !descripcion.contains(terminoBusqueda)) {
                    cumpleFiltros = false;
                }
            }
            
            // Filtro por fecha de inicio (fecha de fin de la tarea)
            if (cumpleFiltros && fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                try {
                    LocalDate fechaInicioFiltro = LocalDate.parse(fechaInicio.trim());
                    if (tarea.getFechaFin() == null || tarea.getFechaFin().isBefore(fechaInicioFiltro)) {
                        cumpleFiltros = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing fechaInicio: " + fechaInicio);
                }
            }
            
            // Filtro por fecha de fin (fecha de fin de la tarea)
            if (cumpleFiltros && fechaFin != null && !fechaFin.trim().isEmpty()) {
                try {
                    LocalDate fechaFinFiltro = LocalDate.parse(fechaFin.trim());
                    if (tarea.getFechaFin() == null || tarea.getFechaFin().isAfter(fechaFinFiltro)) {
                        cumpleFiltros = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing fechaFin: " + fechaFin);
                }
            }
            
            if (cumpleFiltros) {
                tareasFiltradas.add(tarea);
            }
        }
        
        System.out.println("Tareas encontradas: " + tareasFiltradas.size());
        return tareasFiltradas;
    }

    /**
     * @brief Obtiene las tareas "de hoy" para un usuario específico.
     */
    public List<Tarea> getTareasFiltradas(Integer userId) {
        // Implementación del método getTareasFiltradas para obtener tareas de hoy
        // Este método debe ser implementado según la lógica para obtener tareas de hoy
        // Puedes usar el método getTareasDelUsuario para obtener tareas del usuario
        // y luego filtrar las tareas de hoy
        return getTareasDelUsuario(userId);
    }
}