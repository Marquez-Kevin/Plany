package co.plany.plany.service;

import co.plany.plany.model.Tipo;
import co.plany.plany.repository.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoService {

    private final TipoRepository tipoRepository;

    @Autowired
    public TipoService(TipoRepository tipoRepository) {
        this.tipoRepository = tipoRepository;
    }

    public List<Tipo> findAll() {
        return tipoRepository.findAll();
    }
}