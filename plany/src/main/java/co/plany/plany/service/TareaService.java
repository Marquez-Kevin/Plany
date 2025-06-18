package co.plany.plany.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.plany.plany.model.Estado;
import co.plany.plany.model.Recordatorio;
import co.plany.plany.model.Tarea;
import co.plany.plany.model.Usuario;
import co.plany.plany.repository.EstadoRepository;
import co.plany.plany.repository.RecordatorioRepository;
import co.plany.plany.repository.TareaRepository;
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

    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    public Optional<Tarea> getTareaById(Integer id) {
        return tareaRepository.findById(id);
    }

    public Tarea crearTarea(Tarea tarea) {
        if (tarea.getFechaCreacion() == null) {
            tarea.setFechaCreacion(LocalDate.now());
        }

        if (tarea.getRecordatorio() != null && tarea.getRecordatorio().getCodRecor() == null) {
            Recordatorio nuevoRecordatorio = tarea.getRecordatorio();
            Recordatorio recordatorioGuardado = recordatorioRepository.save(nuevoRecordatorio);
            tarea.setRecordatorio(recordatorioGuardado);
        }

        return tareaRepository.save(tarea);
    }

    public Tarea actualizarTarea(Integer id, Tarea tareaDetails) {
        Optional<Tarea> tareaOptional = tareaRepository.findById(id);
        if (tareaOptional.isPresent()) {
            Tarea tareaExistente = tareaOptional.get();
            tareaExistente.setTitulo(tareaDetails.getTitulo());
            tareaExistente.setDescripcion(tareaDetails.getDescripcion());
            tareaExistente.setFechaFin(tareaDetails.getFechaFin());
            if (tareaDetails.getRecordatorio() != null) tareaExistente.setRecordatorio(tareaDetails.getRecordatorio());
            if (tareaDetails.getTipo() != null) tareaExistente.setTipo(tareaDetails.getTipo());
            if (tareaDetails.getPrioridad() != null) tareaExistente.setPrioridad(tareaDetails.getPrioridad());
            if (tareaDetails.getCategoria() != null) tareaExistente.setCategoria(tareaDetails.getCategoria());
            if (tareaDetails.getEstado() != null) tareaExistente.setEstado(tareaDetails.getEstado());

            return tareaRepository.save(tareaExistente);
        }
        return null;
    }

    public void eliminarTarea(Integer id) {
        tareaRepository.deleteById(id);
    }

    public List<Tarea> getTareasDeHoy(Integer userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            Optional<Estado> estadoPendienteOpt = estadoRepository.findByNombreEstado("Pendiente");

            if (estadoPendienteOpt.isPresent()) {
                Estado estadoPendiente = estadoPendienteOpt.get();
                // Obtiene tareas pendientes cuya fecha de fin es hoy o anterior
                return tareaRepository.findByUsuarioAndEstadoAndFechaFinLessThanEqual(usuario, estadoPendiente, LocalDate.now());
            }
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

    public double getProgresoSemanal(Integer userId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(userId);
        if (usuarioOptional.isEmpty()) {
            return 0.0;
        }
        Usuario usuario = usuarioOptional.get();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);

        Optional<Estado> estadoCompletadaOpt = estadoRepository.findByNombreEstado("Completada");

        if (estadoCompletadaOpt.isEmpty()) {
            throw new RuntimeException("Estado 'Completada' no encontrado en la base de datos.");
        }

        Estado estadoCompletada = estadoCompletadaOpt.get();

        // Obtener todas las tareas del usuario en el rango de la última semana (completadas y pendientes)
        List<Tarea> allTasksInWeek = tareaRepository.findByUsuarioAndFechaFinBetween(usuario, startDate, endDate);

        long totalTasks = allTasksInWeek.size();
        long completedTasks = allTasksInWeek.stream()
                                            .filter(t -> t.getEstado() != null && t.getEstado().equals(estadoCompletada))
                                            .count();

        if (totalTasks == 0) {
            return 0.0;
        }

        return (double) completedTasks / totalTasks * 100.0;
    }
}