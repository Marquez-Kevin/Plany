package co.plany.plany.repository;

import co.plany.plany.model.Estado; // Importa la entidad Estado
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Importa Optional

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    // JpaRepository ya proporciona los métodos CRUD básicos para Estado

    /**
     * @brief Busca un estado por su nombre.
     * @param nombreEstado El nombre del estado a buscar (ej. "Completada", "Pendiente").
     * @return Un Optional que contiene el objeto Estado si se encuentra, o un Optional vacío.
     */
    Optional<Estado> findByNombreEstado(String nombreEstado);
}