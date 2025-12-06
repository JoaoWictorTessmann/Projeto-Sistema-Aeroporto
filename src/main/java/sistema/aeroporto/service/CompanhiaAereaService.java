package sistema.aeroporto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.util.CnpjUtils;

@Service
public class CompanhiaAereaService {

    @Autowired
    private CompanhiaAereaRepository companhiaAereaRepository;

    // Listar todas as companhias aéreas
    public List<CompanhiaAerea> listarTodasCompanhias() {
        return companhiaAereaRepository.findAll();
    }

    // Buscar companhia por nome
    public CompanhiaAerea buscarPorNome(String nome) {
        return companhiaAereaRepository.findByNome(nome)
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada"));
    }

    // Buscar companhia por CNPJ
    public CompanhiaAerea buscarPorCnpj(String cnpj) {
        return companhiaAereaRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada"));
    }

    // Salvar nova companhia aérea
    public CompanhiaAerea salvarCompanhia(CompanhiaAerea companhia) {
        if (!CnpjUtils.validarCnpj(companhia.getCnpj())) {
            throw new RuntimeException("CNPJ inválido");
        }
        if (companhiaAereaRepository.existsByCnpj(companhia.getCnpj())) {
            throw new RuntimeException("CNPJ já cadastrado");
        }
        return companhiaAereaRepository.save(companhia);
    }

    // Deletar companhia aérea por ID
    public void deletarCompanhia(Long id) {
        if (!companhiaAereaRepository.existsById(id)) {
            throw new RuntimeException("Companhia não encontrada");
        }
        companhiaAereaRepository.deleteById(id);
    }

    // Atualizar companhia aérea existente
    public CompanhiaAerea atualizarCompanhia(Long id, CompanhiaAerea companhiaAtualizada) {
        CompanhiaAerea companhiaExistente = companhiaAereaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada"));

        companhiaExistente.setStatus(companhiaAtualizada.getStatus());
        // Atualize outros campos conforme necessário

        return companhiaAereaRepository.save(companhiaExistente);
    }
}
