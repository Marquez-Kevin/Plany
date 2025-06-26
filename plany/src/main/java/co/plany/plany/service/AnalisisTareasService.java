package co.plany.plany.service;

import co.plany.plany.dto.SugerenciaRepetitivaDTO;
import co.plany.plany.model.Tarea;
import co.plany.plany.model.Usuario;
import co.plany.plany.repository.TareaRepository;
import co.plany.plany.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnalisisTareasService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<SugerenciaRepetitivaDTO> analizarTareasUsuario(Integer userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (usuarioOpt.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Obtener tareas de los últimos 90 días para tener suficientes datos
        LocalDate fechaInicio = LocalDate.now().minusDays(90);
        List<Tarea> tareasRecientes = tareaRepository.findByUsuarioAndFechaCreacionAfter(usuarioOpt.get(), fechaInicio);

        // 2. Agrupar tareas por título normalizado (ignorar mayúsculas/minúsculas y espacios)
        Map<String, List<Tarea>> tareasAgrupadas = tareasRecientes.stream()
            .collect(Collectors.groupingBy(tarea -> tarea.getTitulo().trim().toLowerCase()));

        List<SugerenciaRepetitivaDTO> sugerencias = new ArrayList<>();

        // 3. Analizar cada grupo de tareas
        for (List<Tarea> grupo : tareasAgrupadas.values()) {
            if (grupo.size() < 3) { // Necesitamos al menos 3 ocurrencias para sugerir un patrón
                continue;
            }

            // Ordenar por fecha de creación
            grupo.sort((t1, t2) -> t1.getFechaCreacion().compareTo(t2.getFechaCreacion()));

            // --- Detección de Patrón Semanal ---
            Map<DayOfWeek, List<Tarea>> porDiaSemana = grupo.stream()
                .collect(Collectors.groupingBy(t -> t.getFechaCreacion().getDayOfWeek()));

            for (Map.Entry<DayOfWeek, List<Tarea>> entry : porDiaSemana.entrySet()) {
                if (entry.getValue().size() >= 3) {
                    long diasPromedio = calcularDiasPromedio(entry.getValue());
                    if (diasPromedio >= 6 && diasPromedio <= 8) { // Patrón semanal
                        String frecuencia = "Cada " + traducirDia(entry.getKey());
                        sugerencias.add(new SugerenciaRepetitivaDTO(grupo.get(0).getTitulo(), frecuencia, grupo.get(0)));
                        break; // Se encontró un patrón para este grupo, pasar al siguiente
                    }
                }
            }
        }
        return sugerencias;
    }

    private long calcularDiasPromedio(List<Tarea> tareas) {
        long totalDias = 0;
        for (int i = 0; i < tareas.size() - 1; i++) {
            totalDias += ChronoUnit.DAYS.between(tareas.get(i).getFechaCreacion(), tareas.get(i+1).getFechaCreacion());
        }
        return totalDias / (tareas.size() - 1);
    }

    private String traducirDia(DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "Lunes";
            case TUESDAY: return "Martes";
            case WEDNESDAY: return "Miércoles";
            case THURSDAY: return "Jueves";
            case FRIDAY: return "Viernes";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }
}

