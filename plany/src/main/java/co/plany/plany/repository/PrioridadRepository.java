package co.plany.plany.repository;

import co.plany.plany.model.Prioridad; // Importa la entidad Prioridad
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioridadRepository extends JpaRepository<Prioridad, Integer> {
    // JpaRepository ya proporciona los métodos CRUD básicos para Prioridad
}

