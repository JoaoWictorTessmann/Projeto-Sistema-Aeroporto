package sistema.aeroporto.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.repository.PilotoRepository;
import sistema.aeroporto.util.CpfUtils;

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

        // --- Validações básicas ---
        if (piloto.getNome() == null || piloto.getNome().isBlank()) {
            throw new RuntimeException("Nome do piloto é obrigatório");
        }

        if (piloto.getCpf() == null || piloto.getCpf().isBlank()) {
            throw new RuntimeException("CPF é obrigatório");
        }

        // Limpa o CPF antes de validar (remove pontos e traços)
        piloto.setCpf(CpfUtils.limpar(piloto.getCpf()));

        if (!CpfUtils.validarCpf(piloto.getCpf())) {
            throw new RuntimeException("CPF inválido");
        }

        // Verificar se CPF já está cadastrado
        if (pilotoRepository.existsByCpf(piloto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // --- Geração de matrícula (caso não exista) ---
        if (piloto.getMatricula() == null || piloto.getMatricula().isBlank()) {
            piloto.setMatricula("TEMP");
        }

        // Primeiro salvamos para gerar o ID
        Piloto saved = pilotoRepository.save(piloto);

        // Agora geramos a matrícula final
        String matriculaGerada = "PIL" + LocalDate.now().getYear() + String.format("%04d", saved.getId());

        saved.setMatricula(matriculaGerada);

        // Atualizamos a matrícula no banco
        return pilotoRepository.save(saved);
    }

    // Método para deletar um piloto por ID
    public void deletarPiloto(Long id) {
        pilotoRepository.deleteById(id);
    }

    // Método para atualizar um piloto existente
    public Piloto atualizarPiloto(Long id, Piloto pilotoAtualizado) {
        Piloto pilotoExistente = pilotoRepository.findById(id).orElse(null);
        if (pilotoExistente != null) {
            pilotoExistente.setDataRenovacao(pilotoAtualizado.getDataRenovacao());
            pilotoExistente.setStatus(pilotoAtualizado.getStatus());
            // Atualize outros campos conforme necessário
            return pilotoRepository.save(pilotoExistente);
        }
        return null;
    }
}
