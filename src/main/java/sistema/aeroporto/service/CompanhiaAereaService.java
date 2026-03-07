package sistema.aeroporto.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.dto.CompanhiaAereaDTO;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.util.CnpjUtils;

@Service
public class CompanhiaAereaService {

    @Autowired
    private CompanhiaAereaRepository companhiaAereaRepository;


    // Buscar companhia por ID
    public CompanhiaAerea buscarPorId(Long id) {
        return companhiaAereaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada"));
    }
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
    public CompanhiaAerea salvarCompanhia(CompanhiaAereaDTO companhiaDTO) {
        if (!CnpjUtils.validarCnpj(companhiaDTO.cnpj())) {
            throw new RuntimeException("CNPJ inválido");
        }
        if (companhiaAereaRepository.existsByCnpj(companhiaDTO.cnpj())) {
            throw new RuntimeException("CNPJ já cadastrado");
        }
        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome(companhiaDTO.nome());
        companhia.setCnpj(companhiaDTO.cnpj());
        companhia.setDataFundacao(LocalDate.now());
        companhia.setSeguroAeronave(companhiaDTO.seguroAeronave());
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        
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
    public CompanhiaAerea atualizarCompanhia(Long id, CompanhiaAereaDTO companhiaAtualizada) {
        CompanhiaAerea companhiaExistente = companhiaAereaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada"));

        companhiaExistente.setNome(companhiaAtualizada.nome());
        companhiaExistente.setCnpj(companhiaAtualizada.cnpj());
        companhiaExistente.setSeguroAeronave(companhiaAtualizada.seguroAeronave());
        companhiaExistente.setStatus(CompanhiaAereaStatus.valueOf(companhiaAtualizada.status().toUpperCase())
    );

        return companhiaAereaRepository.save(companhiaExistente);
    }
}
