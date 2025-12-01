package sistema.aeroporto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.repository.PilotoRepository;

@Service
public class PilotoService {

    @Autowired
    private PilotoRepository pilotoRepository;

    // Método para listar todos os pilotos
    public List<Piloto> listarTodosPilotos() {
        return pilotoRepository.findAll();
    }

    // Método para buscar um piloto por CPF
    public Piloto buscarPorCpf(String cpf) {
        return pilotoRepository.findByCpf(cpf).orElse(null);
    }

    // Método para buscar um piloto por matrícula
    public Piloto buscarPorMatricula(String matricula) {
        return pilotoRepository.findByMatricula(matricula).orElse(null);
    }

    // Método para salvar um novo piloto
    public Piloto salvarPiloto(Piloto piloto) {
        return pilotoRepository.save(piloto);
    }

    // Método para deletar um piloto por ID
    public void deletarPiloto(Long id) {
        pilotoRepository.deleteById(id);
    }
}
