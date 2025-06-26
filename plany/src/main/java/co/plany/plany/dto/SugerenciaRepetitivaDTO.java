package co.plany.plany.dto;

import co.plany.plany.model.Tarea;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SugerenciaRepetitivaDTO {
    private String tituloSugerido;
    private String frecuenciaDetectada; // Ej: "Cada lunes", "Diariamente"
    private Tarea tareaPlantilla; // Una de las tareas encontradas como ejemplo
}
