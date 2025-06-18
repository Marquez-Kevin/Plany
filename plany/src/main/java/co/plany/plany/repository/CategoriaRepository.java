package co.plany.plany.repository;

import co.plany.plany.model.Categoria; // Importa la entidad Categoria
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // JpaRepository ya proporciona los métodos CRUD básicos para Categoria
}