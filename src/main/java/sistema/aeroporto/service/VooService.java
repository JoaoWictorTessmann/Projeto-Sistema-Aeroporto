package sistema.aeroporto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Criar voo com todas as validações
    public Voo criarVoo(Voo voo) {

        if (voo.getOrigem() == null || voo.getDestino() == null) {
            throw new RuntimeException("Origem e destino são obrigatórios");
        }

        if (voo.getOrigem().equalsIgnoreCase(voo.getDestino())) {
            throw new RuntimeException("Origem e destino não podem ser iguais");
        }

        if (voo.getHorarioPartidaPrevisto() == null) {
            throw new RuntimeException("Horário de partida é obrigatório");
        }

        if (voo.getHorarioPartidaPrevisto().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Horário de partida não pode ser no passado");
        }

        if (voo.getPiloto() == null || voo.getPiloto().getId() == null) {
            throw new RuntimeException("Piloto não encontrado");
        }

        Piloto piloto = pilotoRepository.findById(voo.getPiloto().getId())
                .orElseThrow(() -> new RuntimeException("Piloto não encontrado"));

        boolean conflito = vooRepository.findByPiloto_Id(piloto.getId()).stream()
                .anyMatch(v -> v.getHorarioPartidaPrevisto() != null &&
                        v.getHorarioPartidaPrevisto().equals(voo.getHorarioPartidaPrevisto()));

        if (conflito) {
            throw new RuntimeException("Piloto já está escalado para outro voo nesse horário");
        }

        if (voo.getCompanhia() == null || voo.getCompanhia().getId() == null) {
            throw new RuntimeException("Companhia aérea não encontrada");
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findById(voo.getCompanhia().getId())
                .orElseThrow(() -> new RuntimeException("Companhia aérea não encontrada"));

        if (companhia.getStatus() != CompanhiaAereaStatus.ATIVA) {
            throw new RuntimeException("Companhia não está ativa");
        }

        if (voo.getCodigo() == null || voo.getCodigo().isBlank()) {
            throw new RuntimeException("Código do voo é obrigatório");
        }

        if (vooRepository.existsByCodigo(voo.getCodigo())) {
            throw new RuntimeException("Código de voo já existente");
        }

        voo.setStatus(VooStatus.AGENDADO);

        return vooRepository.save(voo);
    }

    public Voo iniciarVoo(Long vooId) {

        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado"));

        if (voo.getStatus() != VooStatus.AGENDADO) {
            throw new RuntimeException("Somente voos agendados podem ser iniciados");
        }

        Piloto piloto = voo.getPiloto();

        if (piloto == null) {
            throw new RuntimeException("Voo sem piloto");
        }

        if (piloto.getStatus() == PilotoStatus.INATIVO ||
                piloto.getStatus() == PilotoStatus.VENCIDO) {
            throw new RuntimeException("Piloto não pode iniciar o voo");
        }

        voo.setStatus(VooStatus.EM_VOO);
        voo.setHorarioPartidaReal(LocalDateTime.now());

        return vooRepository.save(voo);
    }

    // Cancelar voo com motivo obrigatório
    public Voo cancelarVoo(Long vooId, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new RuntimeException("Motivo do cancelamento é obrigatório");
        }

        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado"));

        voo.setStatus(VooStatus.CANCELADO);
        voo.setMotivoCancelamento(motivo);
        return vooRepository.save(voo);
    }

    // Listar todos os voos
    public List<Voo> listarTodos() {
        return vooRepository.findAll();
    }

    // Buscar por id
    public Voo buscarPorId(Long id) {
        return vooRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado"));
    }

    // Buscar voos por status
    public List<Voo> buscarPorStatus(VooStatus status) {
        return vooRepository.findByStatus(status);
    }

    // Buscar voos por piloto
    public List<Voo> buscarPorPiloto(Long pilotoId) {
        return vooRepository.findByPiloto_Id(pilotoId);
    }

    // Buscar voos por companhia
    public List<Voo> buscarPorCompanhia(Long companhiaId) {
        return vooRepository.findByCompanhia_Id(companhiaId);
    }

    // Atualizar informações do voo
    public Voo atualizarVoo(Long vooId, Voo vooAtualizado) {
        Voo vooExistente = vooRepository.findById(vooId)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado"));

        // Atualiza campos permitidos
        vooExistente.setHorarioPartidaPrevisto(vooAtualizado.getHorarioPartidaPrevisto());
        vooExistente.setHorarioChegadaPrevisto(vooAtualizado.getHorarioChegadaPrevisto());
        vooExistente.setHorarioPartidaReal(vooAtualizado.getHorarioPartidaReal());
        vooExistente.setHorarioChegadaReal(vooAtualizado.getHorarioChegadaReal());
        vooExistente.setStatus(vooAtualizado.getStatus());
        // Outros campos conforme necessário

        return vooRepository.save(vooExistente);
    }
}
