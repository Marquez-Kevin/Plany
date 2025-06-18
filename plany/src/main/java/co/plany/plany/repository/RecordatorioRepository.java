package co.plany.plany.repository;

    import co.plany.plany.model.Recordatorio; // Importa la entidad Recordatorio
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface RecordatorioRepository extends JpaRepository<Recordatorio, Integer> {
        // JpaRepository ya proporciona los métodos CRUD básicos para Recordatorio
    }