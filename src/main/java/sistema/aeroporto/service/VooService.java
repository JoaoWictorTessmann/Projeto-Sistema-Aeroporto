package sistema.aeroporto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.dto.request.VooRequest;
import sistema.aeroporto.dto.request.VooUpdateRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.dto.response.PilotoResponse;
import sistema.aeroporto.dto.response.VooResponse;
import sistema.aeroporto.exception.*;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.Voo;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.model.enums.PilotoStatus;
import sistema.aeroporto.model.enums.VooStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.repository.PilotoRepository;
import sistema.aeroporto.repository.VooRepository;

@Service
public class VooService {

    @Autowired
    private VooRepository vooRepository;

    @Autowired
    private PilotoRepository pilotoRepository;

    @Autowired
    private CompanhiaAereaRepository companhiaAereaRepository;

    private VooResponse toResponse(Voo v) {
        PilotoResponse pilotoResponse = v.getPiloto() == null ? null
                : new PilotoResponse(
                        v.getPiloto().getId(),
                        v.getPiloto().getNome(),
                        v.getPiloto().getIdade(),
                        v.getPiloto().getGenero(),
                        v.getPiloto().getCpf(),
                        v.getPiloto().getDataRenovacao(),
                        v.getPiloto().getMatricula(),
                        v.getPiloto().getHabilitacao(),
                        v.getPiloto().getStatus().name());

        CompanhiaAereaResponse companhiaResponse = v.getCompanhia() == null ? null
                : new CompanhiaAereaResponse(
                        v.getCompanhia().getId(),
                        v.getCompanhia().getNome(),
                        v.getCompanhia().getCnpj(),
                        v.getCompanhia().getDataFundacao(),
                        v.getCompanhia().getSeguroAeronave(),
                        v.getCompanhia().getStatus().name());

        return new VooResponse(
                v.getId(),
                pilotoResponse,
                companhiaResponse,
                v.getCodigo(),
                v.getOrigem(),
                v.getDestino(),
                v.getHorarioPartidaPrevisto(),
                v.getHorarioChegadaPrevisto(),
                v.getHorarioPartidaReal(),
                v.getHorarioChegadaReal(),
                v.getMotivoCancelamento(),
                v.getStatus().name());
    }

    public VooResponse criarVoo(VooRequest request) {
        if (request.origem() == null || request.destino() == null) {
            throw new OrigemDestinoObrigatorioException();
        }
        if (request.origem().equalsIgnoreCase(request.destino())) {
            throw new OrigemDestinoIguaisException();
        }
        if (request.horarioPartidaPrevisto() == null) {
            throw new HorarioPartidaObrigatorioException();
        }
        if (request.horarioPartidaPrevisto().isBefore(LocalDateTime.now())) {
            throw new HorarioPartidaPassadoException();
        }
        if (request.pilotoId() == null) {
            throw new PilotoObrigatorioException();
        }
        if (request.codigo() == null || request.codigo().isBlank()) {
            throw new CodigoVooObrigatorioException();
        }
        if (vooRepository.existsByCodigo(request.codigo())) {
            throw new CodigoVooExistenteException();
        }

        Piloto piloto = pilotoRepository.findById(request.pilotoId())
                .orElseThrow(NotFoundPilotoException::new);

        boolean conflito = vooRepository.findByPiloto_Id(piloto.getId()).stream()
                .anyMatch(v -> request.horarioPartidaPrevisto().equals(v.getHorarioPartidaPrevisto()));

        if (conflito) {
            throw new PilotoOutroVooException();
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findById(request.companhiaId())
                .orElseThrow(NotFoundCompanhiaAereaException::new);

        if (companhia.getStatus() != CompanhiaAereaStatus.ATIVA) {
            throw new CompanhiaNaoAtivaException();
        }

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo(request.codigo());
        voo.setOrigem(request.origem().toUpperCase());
        voo.setDestino(request.destino().toUpperCase());
        voo.setHorarioPartidaPrevisto(request.horarioPartidaPrevisto());
        voo.setHorarioChegadaPrevisto(request.horarioChegadaPrevisto() != null
                ? request.horarioChegadaPrevisto()
                : request.horarioPartidaPrevisto().plusHours(4));
        voo.setStatus(VooStatus.AGENDADO);

        return toResponse(vooRepository.save(voo));
    }

    public VooResponse iniciarVoo(Long vooId) {
        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(NotFoundVooException::new);

        if (voo.getStatus() != VooStatus.AGENDADO) {
            throw new SomenteAgendadoException();
        }

        Piloto piloto = voo.getPiloto();
        if (piloto == null) {
            throw new SemPilotoException();
        }
        if (piloto.getStatus() == PilotoStatus.INATIVO) {
            throw new PilotoInativoException();
        }

        voo.setStatus(VooStatus.EM_VOO);
        voo.setHorarioPartidaReal(LocalDateTime.now());

        return toResponse(vooRepository.save(voo));
    }

    public VooResponse finalizarVoo(Long vooId) {
        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(NotFoundVooException::new);

        if (voo.getStatus() != VooStatus.EM_VOO) {
            throw new SomenteEmVooException();
        }

        voo.setStatus(VooStatus.CONCLUIDO);
        voo.setHorarioChegadaReal(LocalDateTime.now());

        return toResponse(vooRepository.save(voo));
    }

    public VooResponse cancelarVoo(Long vooId, String motivoCancelamento) {
        if (motivoCancelamento == null || motivoCancelamento.isBlank()) {
            throw new MotivoCancelamentoObrigatorioException();
        }

        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(NotFoundVooException::new);

        voo.setStatus(VooStatus.CANCELADO);
        voo.setMotivoCancelamento(motivoCancelamento);

        return toResponse(vooRepository.save(voo));
    }

    public List<VooResponse> listarTodos() {
        return vooRepository.findAll().stream().map(this::toResponse).toList();
    }

    public VooResponse buscarPorId(Long id) {
        return toResponse(vooRepository.findById(id)
                .orElseThrow(NotFoundVooException::new));
    }

    public List<VooResponse> buscarPorStatus(String status) {
        return vooRepository.findByStatus(VooStatus.valueOf(status.toUpperCase()))
                .stream().map(this::toResponse).toList();
    }

    public List<VooResponse> buscarPorPiloto(Long pilotoId) {
        if (!pilotoRepository.existsById(pilotoId)) {
            throw new NotFoundPilotoException();
        }
        return vooRepository.findByPiloto_Id(pilotoId)
                .stream().map(this::toResponse).toList();
    }

    public List<VooResponse> buscarPorCompanhia(Long companhiaId) {
        if (!companhiaAereaRepository.existsById(companhiaId)) {
            throw new NotFoundCompanhiaAereaException();
        }
        return vooRepository.findByCompanhia_Id(companhiaId)
                .stream().map(this::toResponse).toList();
    }

    public VooResponse atualizarVoo(Long vooId, VooUpdateRequest request) {
        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(NotFoundVooException::new);

        if (request.horarioPartidaReal() != null) {
            voo.setHorarioPartidaReal(request.horarioPartidaReal());
        }
        if (request.horarioChegadaReal() != null) {
            voo.setHorarioChegadaReal(request.horarioChegadaReal());
        }
        if (request.status() != null && !request.status().isBlank()) {
            voo.setStatus(VooStatus.valueOf(request.status().toUpperCase()));
        }

        return toResponse(vooRepository.save(voo));

    }
}