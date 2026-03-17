package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sistema.aeroporto.dto.request.VooRequest;
import sistema.aeroporto.dto.response.VooResponse;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.Voo;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.model.enums.PilotoStatus;
import sistema.aeroporto.model.enums.VooStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.repository.PilotoRepository;
import sistema.aeroporto.repository.VooRepository;

public class VooServiceTeste {

    @InjectMocks
    private VooService vooService;

    @Mock
    private VooRepository vooRepository;

    @Mock
    private PilotoRepository pilotoRepository;

    @Mock
    private CompanhiaAereaRepository companhiaAereaRepository;

    private Piloto pilotoAtivo;
    private Piloto pilotoInativo;
    private CompanhiaAerea companhiaAtiva;
    private VooRequest vooRequest;

    private static final Long PILOTO_ID    = 1L;
    private static final Long COMPANHIA_ID = 10L;
    private static final String CODIGO_VOO = "VOO001";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        pilotoAtivo = new Piloto();
        pilotoAtivo.setId(PILOTO_ID);
        pilotoAtivo.setNome("João");
        pilotoAtivo.setStatus(PilotoStatus.ATIVO);

        pilotoInativo = new Piloto();
        pilotoInativo.setId(2L);
        pilotoInativo.setNome("Maria");
        pilotoInativo.setStatus(PilotoStatus.INATIVO);

        companhiaAtiva = new CompanhiaAerea();
        companhiaAtiva.setId(COMPANHIA_ID);
        companhiaAtiva.setNome("Azul");
        companhiaAtiva.setStatus(CompanhiaAereaStatus.ATIVA);

        // VooRequest agora usa IDs diretamente
        vooRequest = new VooRequest(
                PILOTO_ID, COMPANHIA_ID,
                CODIGO_VOO,
                "São Paulo", "Rio de Janeiro",
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(4),
                null, null, null,
                VooStatus.AGENDADO.name());
    }

    // =========================================================================
    // criarVoo
    // =========================================================================

    @Test
    void deveCriarVooComSucesso() {
        Voo vooSalvo = new Voo();
        vooSalvo.setId(5L);
        vooSalvo.setCodigo(CODIGO_VOO);
        vooSalvo.setStatus(VooStatus.AGENDADO);
        vooSalvo.setPiloto(pilotoAtivo);
        vooSalvo.setCompanhia(companhiaAtiva);

        when(pilotoRepository.findById(PILOTO_ID)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(PILOTO_ID)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(COMPANHIA_ID)).thenReturn(Optional.of(companhiaAtiva));
        when(vooRepository.existsByCodigo(CODIGO_VOO)).thenReturn(false);
        when(vooRepository.save(any(Voo.class))).thenReturn(vooSalvo);

        VooResponse criado = vooService.criarVoo(vooRequest);

        assertNotNull(criado);
        assertEquals(CODIGO_VOO, criado.codigo());
        assertEquals(VooStatus.AGENDADO.name(), criado.status());
    }

    @Test
    void deveLancarErroQuandoPilotoNaoExiste() {
        when(pilotoRepository.findById(PILOTO_ID)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooRequest));

        assertEquals("Piloto não encontrado", e.getMessage());
    }

    @Test
    void deveLancarErroQuandoOrigemIgualDestino() {
        VooRequest invalido = new VooRequest(PILOTO_ID, COMPANHIA_ID, CODIGO_VOO,
                "Rio de Janeiro", "Rio de Janeiro",
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(4),
                null, null, null, null);

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(invalido));

        assertEquals("Origem e destino não podem ser iguais", e.getMessage());
    }

    @Test
    void deveLancarErroQuandoHorarioNoPassado() {
        VooRequest invalido = new VooRequest(PILOTO_ID, COMPANHIA_ID, CODIGO_VOO,
                "São Paulo", "Rio de Janeiro",
                LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2),
                null, null, null, null);

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(invalido));

        assertEquals("Horário de partida não pode ser no passado", e.getMessage());
    }

    @Test
    void deveLancarErroConflitoDeHorario() {
        Voo vooExistente = new Voo();
        vooExistente.setHorarioPartidaPrevisto(vooRequest.horarioPartidaPrevisto());

        when(pilotoRepository.findById(PILOTO_ID)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(PILOTO_ID)).thenReturn(List.of(vooExistente));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooRequest));

        assertEquals("Piloto já está escalado para outro voo nesse horário", e.getMessage());
    }

    @Test
    void deveLancarErroQuandoCompanhiaNaoExiste() {
        when(pilotoRepository.findById(PILOTO_ID)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(PILOTO_ID)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(COMPANHIA_ID)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooRequest));

        assertEquals("Companhia não encontrada", e.getMessage());
    }

    @Test
    void deveLancarErroQuandoCompanhiaInativa() {
        CompanhiaAerea inativa = new CompanhiaAerea();
        inativa.setId(COMPANHIA_ID);
        inativa.setStatus(CompanhiaAereaStatus.INATIVA);

        when(pilotoRepository.findById(PILOTO_ID)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(PILOTO_ID)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(COMPANHIA_ID)).thenReturn(Optional.of(inativa));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooRequest));

        assertEquals("Companhia não está ativa", e.getMessage());
    }

    @Test
    void deveLancarErroCodigoDuplicado() {
        when(pilotoRepository.findById(PILOTO_ID)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(PILOTO_ID)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(COMPANHIA_ID)).thenReturn(Optional.of(companhiaAtiva));
        when(vooRepository.existsByCodigo(CODIGO_VOO)).thenReturn(true);

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooRequest));

        assertEquals("Código de voo já existente", e.getMessage());
    }

    // =========================================================================
    // iniciarVoo
    // =========================================================================

    @Test
    void deveIniciarVooComSucesso() {
        Voo voo = new Voo();
        voo.setId(5L);
        voo.setStatus(VooStatus.AGENDADO);
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);

        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        VooResponse iniciado = vooService.iniciarVoo(5L);

        assertEquals(VooStatus.EM_VOO.name(), iniciado.status());
        assertNotNull(iniciado.horarioPartidaReal());
    }

    @Test
    void deveLancarErroAoIniciarVooNaoEncontrado() {
        when(vooRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.iniciarVoo(99L));

        assertEquals("Voo não encontrado", e.getMessage());
    }

    @Test
    void deveLancarErroPilotoInativoAoIniciar() {
        Voo voo = new Voo();
        voo.setId(5L);
        voo.setStatus(VooStatus.AGENDADO);
        voo.setPiloto(pilotoInativo);

        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.iniciarVoo(5L));

        assertEquals("Piloto não pode iniciar o voo", e.getMessage());
    }

    @Test
    void deveLancarErroIniciarVooNaoAgendado() {
        Voo voo = new Voo();
        voo.setId(5L);
        voo.setStatus(VooStatus.CANCELADO);
        voo.setPiloto(pilotoAtivo);

        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.iniciarVoo(5L));

        assertEquals("Somente voos agendados podem ser iniciados", e.getMessage());
    }

    // =========================================================================
    // cancelarVoo
    // =========================================================================

    @Test
    void deveCancelarVooComSucesso() {
        Voo voo = new Voo();
        voo.setId(5L);
        voo.setStatus(VooStatus.AGENDADO);
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);

        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        VooResponse cancelado = vooService.cancelarVoo(5L, "Mau tempo");

        assertEquals(VooStatus.CANCELADO.name(), cancelado.status());
        assertEquals("Mau tempo", cancelado.motivoCancelamento());
    }

    @Test
    void deveLancarErroSemMotivoCancelamento() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.cancelarVoo(5L, ""));

        assertEquals("Motivo do cancelamento é obrigatório", e.getMessage());
    }

    @Test
    void deveLancarErroCancelamentoVooNaoEncontrado() {
        when(vooRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.cancelarVoo(99L, "Motivo qualquer"));

        assertEquals("Voo não encontrado", e.getMessage());
    }

    // =========================================================================
    // listarTodos / buscarPorStatus / buscarPorPiloto / buscarPorCompanhia
    // =========================================================================

    @Test
    void deveListarTodosVoos() {
        Voo voo = new Voo();
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);
        voo.setStatus(VooStatus.AGENDADO);

        when(vooRepository.findAll()).thenReturn(List.of(voo, voo));

        List<VooResponse> lista = vooService.listarTodos();

        assertEquals(2, lista.size());
    }

    @Test
    void deveBuscarPorStatus() {
        Voo voo = new Voo();
        voo.setStatus(VooStatus.AGENDADO);
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);

        when(vooRepository.findByStatus(VooStatus.AGENDADO)).thenReturn(List.of(voo));

        List<VooResponse> encontrados = vooService.buscarPorStatus("AGENDADO");

        assertEquals(1, encontrados.size());
        assertEquals(VooStatus.AGENDADO.name(), encontrados.get(0).status());
    }

    @Test
    void deveBuscarPorPiloto() {
        Voo voo = new Voo();
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);
        voo.setStatus(VooStatus.AGENDADO);

        when(vooRepository.findByPiloto_Id(PILOTO_ID)).thenReturn(List.of(voo));

        List<VooResponse> encontrados = vooService.buscarPorPiloto(PILOTO_ID);

        assertEquals(1, encontrados.size());
    }

    @Test
    void deveBuscarPorCompanhia() {
        Voo voo = new Voo();
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);
        voo.setStatus(VooStatus.AGENDADO);

        when(vooRepository.findByCompanhia_Id(COMPANHIA_ID)).thenReturn(List.of(voo));

        List<VooResponse> encontrados = vooService.buscarPorCompanhia(COMPANHIA_ID);

        assertEquals(1, encontrados.size());
    }

    // =========================================================================
    // atualizarVoo
    // =========================================================================

    @Test
    void deveAtualizarVooComSucesso() {
        Voo vooExistente = new Voo();
        vooExistente.setId(5L);
        vooExistente.setStatus(VooStatus.AGENDADO);
        vooExistente.setPiloto(pilotoAtivo);
        vooExistente.setCompanhia(companhiaAtiva);

        VooRequest request = new VooRequest(PILOTO_ID, COMPANHIA_ID, CODIGO_VOO,
                "São Paulo", "Rio de Janeiro",
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(6),
                LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(10),
                null, "CONCLUIDO");

        when(vooRepository.findById(5L)).thenReturn(Optional.of(vooExistente));
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        VooResponse retorno = vooService.atualizarVoo(5L, request);

        assertEquals(VooStatus.CONCLUIDO.name(), retorno.status());
    }

    @Test
    void deveLancarErroAtualizarVooNaoEncontrado() {
        VooRequest request = new VooRequest(PILOTO_ID, COMPANHIA_ID, CODIGO_VOO,
                "São Paulo", "Rio de Janeiro",
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(5),
                null, "CONCLUIDO");

        when(vooRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.atualizarVoo(99L, request));

        assertEquals("Voo não encontrado", e.getMessage());
    }
}