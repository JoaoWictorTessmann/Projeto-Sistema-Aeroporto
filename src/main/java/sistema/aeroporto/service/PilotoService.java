package sistema.aeroporto.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.dto.request.PilotoRequest;
import sistema.aeroporto.dto.request.PilotoUpdateRequest;
import sistema.aeroporto.dto.response.PilotoResponse;
import sistema.aeroporto.exception.CpfInvalidoException;
import sistema.aeroporto.exception.CpfJaCadastradoException;
import sistema.aeroporto.exception.CpfObrigatorioException;
import sistema.aeroporto.exception.MenorIdadeException;
import sistema.aeroporto.exception.NomeObrigatorioException;
import sistema.aeroporto.exception.NotFoundPilotoException;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.enums.PilotoStatus;
import sistema.aeroporto.repository.PilotoRepository;
import sistema.aeroporto.util.CpfUtils;

@Service
public class PilotoService {

    @Autowired
    private PilotoRepository pilotoRepository;

    // Converte entity → Response
    private PilotoResponse toResponse(Piloto p) {
        return new PilotoResponse(
                p.getId(),
                p.getNome(),
                p.getIdade(),
                p.getGenero(),
                p.getCpf(),
                p.getDataRenovacao(),
                p.getMatricula(),
                p.getHabilitacao(),
                p.getStatus().name());
    }

    public PilotoResponse buscarPorId(Long id) {
        return toResponse(pilotoRepository.findById(id)
                .orElseThrow(NotFoundPilotoException::new));
    }

    public List<PilotoResponse> listarTodosPilotos() {
        return pilotoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PilotoResponse buscarPorCpf(String cpf) {
        String cpfLimpo = CpfUtils.limpar(cpf);
        return toResponse(pilotoRepository.findByCpf(cpfLimpo)
                .orElseThrow(NotFoundPilotoException::new));
    }

    public PilotoResponse buscarPorMatricula(String matricula) {
        return toResponse(pilotoRepository.findByMatricula(matricula)
                .orElseThrow(NotFoundPilotoException::new));
    }

    public Piloto buscarEntidadePorCpf(String cpf) {
        String cpfLimpo = CpfUtils.limpar(cpf);
        return pilotoRepository.findByCpf(cpfLimpo)
                .orElseThrow(NotFoundPilotoException::new);
    }

    public PilotoResponse salvarPiloto(PilotoRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new NomeObrigatorioException();
        }
        if (request.cpf() == null || request.cpf().isBlank()) {
            throw new CpfObrigatorioException();
        }
        if (request.idade() != null && request.idade() < 18) {
            throw new MenorIdadeException();
        }

        String cpfLimpo = CpfUtils.limpar(request.cpf());

        if (!CpfUtils.validarCpf(cpfLimpo)) {
            throw new CpfInvalidoException();
        }
        if (pilotoRepository.existsByCpf(cpfLimpo)) {
            throw new CpfJaCadastradoException();
        }

        Piloto piloto = new Piloto();
        piloto.setNome(request.nome());
        piloto.setIdade(request.idade());
        piloto.setGenero(request.genero());
        piloto.setCpf(cpfLimpo);
        piloto.setDataRenovacao(LocalDate.now());
        piloto.setHabilitacao(request.habilitacao());
        piloto.setMatricula(UUID.randomUUID().toString());

        if (request.status() != null && !request.status().isBlank()) {
            piloto.setStatus(PilotoStatus.valueOf(request.status().toUpperCase()));
        } else {
            piloto.setStatus(PilotoStatus.ATIVO);
        }

        Piloto saved = pilotoRepository.save(piloto);

        saved.setMatricula("PIL" + LocalDate.now().getYear() + String.format("%04d", saved.getId()));

        return toResponse(pilotoRepository.save(saved));
    }

    public void deletarPiloto(Long id) {
        if (!pilotoRepository.existsById(id)) {
            throw new NotFoundPilotoException();
        }
        pilotoRepository.deleteById(id);
    }

    public PilotoResponse atualizarPiloto(Long id, PilotoUpdateRequest request) {
        Piloto piloto = pilotoRepository.findById(id)
                .orElseThrow(NotFoundPilotoException::new);

        piloto.setNome(request.nome());
        piloto.setIdade(request.idade());
        piloto.setGenero(request.genero());
        piloto.setStatus(PilotoStatus.valueOf(request.status().toUpperCase()));

        return toResponse(pilotoRepository.save(piloto));
    }
}