package co.plany.plany.repository;

import co.plany.plany.model.Tipo; // Importa la entidad Tipo
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoRepository extends JpaRepository<Tipo, Integer> {
    // JpaRepository ya proporciona los métodos CRUD básicos para Tipo
}
