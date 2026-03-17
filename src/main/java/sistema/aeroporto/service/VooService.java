package sistema.aeroporto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sistema.aeroporto.dto.VooDTO;
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
    public Voo criarVoo(VooDTO vooDTO) {

        if (vooDTO.origem() == null || vooDTO.destino() == null) {
            throw new RuntimeException("Origem e destino são obrigatórios");
        }

        if (vooDTO.origem().equalsIgnoreCase(vooDTO.destino())) {
            throw new RuntimeException("Origem e destino não podem ser iguais");
        }

        if (vooDTO.horarioPartidaPrevisto() == null) {
            throw new RuntimeException("Horário de partida é obrigatório");
        }

        LocalDateTime horarioPartidaPrevisto = LocalDateTime.parse(vooDTO.horarioPartidaPrevisto());

        if (horarioPartidaPrevisto.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Horário de partida não pode ser no passado");
        }

        if (vooDTO.piloto() == null || vooDTO.piloto().cpf() == null) {
            throw new RuntimeException("Piloto é obrigatório");
        }

        Piloto piloto = pilotoRepository.findByCpf(vooDTO.piloto().cpf())
                .orElseThrow(() -> new RuntimeException("Piloto não encontrado"));

        boolean conflito = vooRepository.findByPiloto_Id(piloto.getId()).stream()
                .anyMatch(v -> v.getHorarioPartidaPrevisto() != null &&
                        v.getHorarioPartidaPrevisto().equals(horarioPartidaPrevisto));

        if (conflito) {
            throw new RuntimeException("Piloto já está escalado para outro voo nesse horário");
        }

        if (vooDTO.companhia() == null || vooDTO.companhia().cnpj() == null) {
            throw new RuntimeException("Companhia aérea não encontrada");
        }

        CompanhiaAerea companhia = companhiaAereaRepository.findByCnpj(vooDTO.companhia().cnpj())
                .orElseThrow(() -> new RuntimeException("Companhia aérea não encontrada"));

        if (companhia.getStatus() != CompanhiaAereaStatus.ATIVA) {
            throw new RuntimeException("Companhia não está ativa");
        }

        if (vooDTO.codigo() == null || vooDTO.codigo().isBlank()) {
            throw new RuntimeException("Código do voo é obrigatório");
        }

        if (vooRepository.existsByCodigo(vooDTO.codigo())) {
            throw new RuntimeException("Código de voo já existente");
        }

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo(vooDTO.codigo());
        voo.setOrigem(vooDTO.origem());
        voo.setDestino(vooDTO.destino());
        voo.setHorarioPartidaPrevisto(horarioPartidaPrevisto);
        voo.setHorarioChegadaPrevisto(horarioPartidaPrevisto.plusHours(4));
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

        if (piloto.getStatus() == PilotoStatus.INATIVO) {
            throw new RuntimeException("Piloto não pode iniciar o voo");
        }

        voo.setStatus(VooStatus.EM_VOO);
        voo.setHorarioPartidaReal(LocalDateTime.now());

        return vooRepository.save(voo);
    }

    // Cancelar voo com motivo obrigatório
    public Voo cancelarVoo(Long vooId, VooDTO motivo) {
        if (motivo == null || motivo.motivoCancelamento() == null || motivo.motivoCancelamento().trim().isEmpty()) {
            throw new RuntimeException("Motivo do cancelamento é obrigatório");
        }

        Voo voo = vooRepository.findById(vooId)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado"));

        voo.setStatus(VooStatus.CANCELADO);
        voo.setMotivoCancelamento(motivo.motivoCancelamento());
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
    public List<Voo> buscarPorStatus(VooDTO status) {
        return vooRepository.findByStatus(VooStatus.valueOf(status.status()));
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
    public Voo atualizarVoo(Long vooId, VooDTO vooAtualizado) {
        Voo vooExistente = vooRepository.findById(vooId)
                .orElseThrow(() -> new RuntimeException("Voo não encontrado"));

        // Atualiza campos permitidos
        LocalDateTime partidaReal =
            LocalDateTime.parse(vooAtualizado.horarioPartidaReal());

        LocalDateTime chegadaReal =
            LocalDateTime.parse(vooAtualizado.horarioChegadaReal());

            vooExistente.setHorarioPartidaReal(partidaReal);
            vooExistente.setHorarioChegadaReal(chegadaReal);
            vooExistente.setStatus(VooStatus.valueOf(vooAtualizado.status().toUpperCase()));

        return vooRepository.save(vooExistente);
    }
}
