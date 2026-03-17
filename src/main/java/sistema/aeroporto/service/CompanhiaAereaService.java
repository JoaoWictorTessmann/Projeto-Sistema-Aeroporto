package sistema.aeroporto.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.dto.request.CompanhiaAereaRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.exception.CnpjInvalidoException;
import sistema.aeroporto.exception.CnpjJaCadastradoException;
import sistema.aeroporto.exception.NomeJaCadastradoException;
import sistema.aeroporto.exception.NotFoundCompanhiaAereaException;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.util.CnpjUtils;

@Service
public class CompanhiaAereaService {

    @Autowired
    private CompanhiaAereaRepository companhiaAereaRepository;

    // Converte entity → Response
    private CompanhiaAereaResponse toResponse(CompanhiaAerea c) {
        return new CompanhiaAereaResponse(
            c.getId(),
            c.getNome(),
            c.getCnpj(),
            c.getDataFundacao(),
            c.getSeguroAeronave(),
            c.getStatus().name()
        );
    }

    public CompanhiaAereaResponse buscarPorId(Long id) {
        CompanhiaAerea c = companhiaAereaRepository.findById(id)
                .orElseThrow(NotFoundCompanhiaAereaException::new);
        return toResponse(c);
    }

    public List<CompanhiaAereaResponse> listarTodasCompanhias() {
        return companhiaAereaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CompanhiaAereaResponse buscarPorNome(String nome) {
        CompanhiaAerea c = companhiaAereaRepository.findByNome(nome)
                .orElseThrow(NotFoundCompanhiaAereaException::new);
        return toResponse(c);
    }

    public CompanhiaAereaResponse buscarPorCnpj(String cnpj) {
        CompanhiaAerea c = companhiaAereaRepository.findByCnpj(cnpj)
                .orElseThrow(NotFoundCompanhiaAereaException::new);
        return toResponse(c);
    }

    public CompanhiaAerea buscarEntidadePorCnpj(String cnpj) {
        return companhiaAereaRepository.findByCnpj(cnpj)
                .orElseThrow(NotFoundCompanhiaAereaException::new);
    }

    public CompanhiaAereaResponse salvarCompanhia(CompanhiaAereaRequest request) {
        if (!CnpjUtils.validarCnpj(request.cnpj())) {
            throw new CnpjInvalidoException();
        }
        if (companhiaAereaRepository.existsByCnpj(request.cnpj())) {
            throw new CnpjJaCadastradoException();
        }
        if (companhiaAereaRepository.existsByNome(request.nome())) {
            throw new NomeJaCadastradoException();
        }

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome(request.nome());
        companhia.setCnpj(request.cnpj());
        companhia.setDataFundacao(request.dataFundacao() != null ? request.dataFundacao() : LocalDate.now());
        companhia.setSeguroAeronave(request.seguroAeronave());

        if (request.status() != null && !request.status().isBlank()) {
            companhia.setStatus(CompanhiaAereaStatus.valueOf(request.status().toUpperCase()));
        } else {
            companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        }

        return toResponse(companhiaAereaRepository.save(companhia));
    }

    public void deletarCompanhia(Long id) {
        if (!companhiaAereaRepository.existsById(id)) {
            throw new NotFoundCompanhiaAereaException();
        }
        companhiaAereaRepository.deleteById(id);
    }

    public CompanhiaAereaResponse atualizarCompanhia(Long id, CompanhiaAereaRequest request) {
        CompanhiaAerea companhia = companhiaAereaRepository.findById(id)
                .orElseThrow(NotFoundCompanhiaAereaException::new);

        companhia.setNome(request.nome());
        companhia.setCnpj(request.cnpj());
        companhia.setSeguroAeronave(request.seguroAeronave());
        companhia.setStatus(CompanhiaAereaStatus.valueOf(request.status().toUpperCase()));

        return toResponse(companhiaAereaRepository.save(companhia));
    }
}