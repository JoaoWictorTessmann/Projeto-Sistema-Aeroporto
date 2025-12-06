package sistema.aeroporto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.Voo;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.model.enums.PilotoStatus;
import sistema.aeroporto.model.enums.VooStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.repository.PilotoRepository;
import sistema.aeroporto.repository.VooRepository;
import sistema.aeroporto.service.VooService;

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
    private CompanhiaAerea companhiaAtiva;
    private Voo voo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        pilotoAtivo = new Piloto();
        pilotoAtivo.setId(1L);
        pilotoAtivo.setStatus(PilotoStatus.ATIVO);

        companhiaAtiva = new CompanhiaAerea();
        companhiaAtiva.setId(10L);
        companhiaAtiva.setStatus(CompanhiaAereaStatus.ATIVA);

        voo = new Voo();
        voo.setId(5L);
        voo.setCodigo("VOO001");
        voo.setPiloto(pilotoAtivo);
        voo.setCompanhia(companhiaAtiva);
        voo.setOrigem("São Paulo");
        voo.setDestino("Rio de Janeiro");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));
    }

    // ------------------------------------------------------------
    // 1 - CRIAR VOO
    // ------------------------------------------------------------

    /*
     * ============================
     * TESTE: Criar voo com sucesso
     * OBJETIVO: Garantir que um voo válido é criado corretamente.
     * CONDIÇÕES: Piloto ativo, sem conflitos, companhia ativa, código único.
     * RESULTADO ESPERADO: Voo salvo com status AGENDADO.
     * ============================
     */
    @Test
    void deveCriarVooComSucesso() {
        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(10L)).thenReturn(Optional.of(companhiaAtiva));
        when(vooRepository.existsByCodigo("VOO001")).thenReturn(false);
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        Voo criado = vooService.criarVoo(voo);

        assertEquals(VooStatus.AGENDADO, criado.getStatus());
    }

    /*
     * ============================
     * TESTE: Erro quando piloto não existe
     * OBJETIVO: Impedir criação de voo para piloto inexistente.
     * CONDIÇÕES: Piloto retornando Optional.empty().
     * RESULTADO ESPERADO: Lançar RuntimeException "Piloto não encontrado".
     * ============================
     */
    @Test
    void deveLancarErroQuandoPilotoNaoExiste() {
        when(pilotoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Piloto não encontrado", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro quando piloto está inativo
     * OBJETIVO: Não permitir criação de voo com piloto inativo.
     * CONDIÇÕES: Piloto com status INATIVO.
     * RESULTADO ESPERADO: Lançar RuntimeException adequada.
     * ============================
     */
    @Test
    void deveLancarErroQuandoPilotoInativo() {
        pilotoAtivo.setStatus(PilotoStatus.INATIVO);
        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Piloto não está apto para voar", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro por conflito de horário
     * OBJETIVO: Impedir piloto de voar em dois voos no mesmo horário.
     * CONDIÇÕES: Outro voo com horário igual.
     * RESULTADO ESPERADO: Exceção de conflito.
     * ============================
     */
    @Test
    void deveLancarErroPorConflitoDeHorario() {
        Voo outro = new Voo();
        outro.setHorarioPartidaPrevisto(voo.getHorarioPartidaPrevisto());

        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of(outro));

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Piloto já está escalado para outro voo nesse horário", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro quando companhia não existe
     * OBJETIVO: Validar existência da companhia aérea.
     * CONDIÇÕES: findById retornando Optional.empty().
     * RESULTADO ESPERADO: Exceção "Companhia aérea não encontrada".
     * ============================
     */
    @Test
    void deveLancarErroQuandoCompanhiaNaoExiste() {
        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Companhia aérea não encontrada", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro quando companhia está inativa
     * OBJETIVO: Impedir criação de voo para companhia inativa.
     * CONDIÇÕES: Companhia com status INATIVA.
     * RESULTADO ESPERADO: Exceção "Companhia não está ativa".
     * ============================
     */
    @Test
    void deveLancarErroQuandoCompanhiaInativa() {
        companhiaAtiva.setStatus(CompanhiaAereaStatus.INATIVA);

        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(10L)).thenReturn(Optional.of(companhiaAtiva));

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Companhia não está ativa", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro por código duplicado
     * OBJETIVO: Impedir criação com código já existente.
     * CONDIÇÕES: existsByCodigo retornando true.
     * RESULTADO ESPERADO: Exceção "Código de voo já existente".
     * ============================
     */
    @Test
    void deveLancarErroCodigoDuplicado() {
        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));
        when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
        when(companhiaAereaRepository.findById(10L)).thenReturn(Optional.of(companhiaAtiva));
        when(vooRepository.existsByCodigo("VOO001")).thenReturn(true);

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Código de voo já existente", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Origem igual ao destino
     * OBJETIVO: Validar que origem e destino sejam diferentes.
     * CONDIÇÕES: Origem == destino.
     * RESULTADO ESPERADO: Exceção adequada.
     * ============================
     */
    @Test
    void deveLancarErroOrigemIgualDestino() {
        voo.setOrigem("Rio");
        voo.setDestino("Rio");

        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Origem e destino não podem ser iguais", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Horário no passado
     * OBJETIVO: Evitar criar voo com horário anterior ao atual.
     * CONDIÇÕES: Data < agora.
     * RESULTADO ESPERADO: Exceção "Horário de partida não pode ser no passado".
     * ============================
     */
    @Test
    void deveLancarErroHorarioNoPassado() {
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().minusHours(1));

        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(pilotoAtivo));

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.criarVoo(voo));
        assertEquals("Horário de partida não pode ser no passado", e.getMessage());
    }

    // ------------------------------------------------------------
    // 2 - INICIAR VOO
    // ------------------------------------------------------------

    /*
     * ============================
     * TESTE: Iniciar voo com sucesso
     * OBJETIVO: Verificar mudança para status EM_VOO.
     * CONDIÇÕES: Voo existente com piloto ativo.
     * RESULTADO ESPERADO: Status alterado e horário real preenchido.
     * ============================
     */
    @Test
    void deveIniciarVooComSucesso() {
        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        Voo iniciado = vooService.iniciarVoo(5L);

        assertEquals(VooStatus.EM_VOO, iniciado.getStatus());
        assertNotNull(iniciado.getHorarioPartidaReal());
    }

    /*
     * ============================
     * TESTE: Erro ao iniciar voo inexistente
     * OBJETIVO: Garantir que voo deve existir para ser iniciado.
     * CONDIÇÕES: findById retornando empty.
     * RESULTADO ESPERADO: Exceção "Voo não encontrado".
     * ============================
     */
    @Test
    void deveLancarErroAoIniciarVooNaoEncontrado() {
        when(vooRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.iniciarVoo(5L));
        assertEquals("Voo não encontrado", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro piloto inativo ao iniciar voo
     * OBJETIVO: Garantir que voo não inicie com piloto inativo.
     * CONDIÇÕES: Piloto com status INATIVO.
     * RESULTADO ESPERADO: Exceção apropriada.
     * ============================
     */
    @Test
    void deveLancarErroPilotoInativoAoIniciar() {
        pilotoAtivo.setStatus(PilotoStatus.INATIVO);
        voo.setPiloto(pilotoAtivo);

        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.iniciarVoo(5L));
        assertEquals("Piloto não pode iniciar o voo", e.getMessage());
    }

    // ------------------------------------------------------------
    // 3 - CANCELAR VOO
    // ------------------------------------------------------------

    /*
     * ============================
     * TESTE: Cancelar voo com sucesso
     * OBJETIVO: Cancelar voo existente com motivo informado.
     * CONDIÇÕES: Voo encontrado.
     * RESULTADO ESPERADO: Status CANCELADO e motivo registrado.
     * ============================
     */
    @Test
    void deveCancelarVooComSucesso() {
        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        Voo cancelado = vooService.cancelarVoo(5L, "Mau tempo");

        assertEquals(VooStatus.CANCELADO, cancelado.getStatus());
        assertEquals("Mau tempo", cancelado.getMotivoCancelamento());
    }

    /*
     * ============================
     * TESTE: Erro ao cancelar sem motivo
     * OBJETIVO: Motivo é obrigatório.
     * CONDIÇÕES: String vazia.
     * RESULTADO ESPERADO: Exceção.
     * ============================
     */
    @Test
    void deveLancarErroSemMotivoCancelamento() {
        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.cancelarVoo(5L, ""));
        assertEquals("Motivo do cancelamento é obrigatório", e.getMessage());
    }

    /*
     * ============================
     * TESTE: Erro cancelar voo inexistente
     * OBJETIVO: Não permitir cancelar voo que não existe.
     * CONDIÇÕES: findById vazio.
     * RESULTADO ESPERADO: Exceção "Voo não encontrado".
     * ============================
     */
    @Test
    void deveLancarErroCancelamentoVooNaoEncontrado() {
        when(vooRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.cancelarVoo(5L, "Motivo"));
        assertEquals("Voo não encontrado", e.getMessage());
    }

    // ------------------------------------------------------------
    // 4 - LISTAGENS
    // ------------------------------------------------------------

    /*
     * ============================
     * TESTE: Listar todos os voos
     * OBJETIVO: Retornar todos os voos cadastrados.
     * CONDIÇÕES: Mock com dois voos.
     * RESULTADO ESPERADO: Lista com 2 itens.
     * ============================
     */
    @Test
    void deveListarTodosVoos() {
        when(vooRepository.findAll()).thenReturn(List.of(new Voo(), new Voo()));

        List<Voo> lista = vooService.listarTodos();

        assertEquals(2, lista.size());
    }

    /*
     * ============================
     * TESTE: Buscar por status
     * OBJETIVO: Retornar voos com status específico.
     * CONDIÇÕES: Mock com 1 voo AGENDADO.
     * RESULTADO ESPERADO: Lista com 1 item.
     * ============================
     */
    @Test
    void deveBuscarPorStatus() {
        when(vooRepository.findByStatus(VooStatus.AGENDADO))
                .thenReturn(List.of(voo));

        List<Voo> encontrados = vooService.buscarPorStatus(VooStatus.AGENDADO);

        assertEquals(1, encontrados.size());
    }

    /*
     * ============================
     * TESTE: Buscar por piloto
     * OBJETIVO: Listar voos de um piloto específico.
     * CONDIÇÕES: Mock retornando 1 voo.
     * RESULTADO ESPERADO: Lista com 1 item.
     * ============================
     */
    @Test
    void deveBuscarPorPiloto() {
        when(vooRepository.findByPiloto_Id(1L))
                .thenReturn(List.of(voo));

        List<Voo> encontrados = vooService.buscarPorPiloto(1L);

        assertEquals(1, encontrados.size());
    }

    /*
     * ============================
     * TESTE: Buscar por companhia aérea
     * OBJETIVO: Retornar voos de uma companhia específica.
     * CONDIÇÕES: Mock com 1 voo.
     * RESULTADO ESPERADO: Lista com 1 elemento.
     * ============================
     */
    @Test
    void deveBuscarPorCompanhia() {
        when(vooRepository.findByCompanhia_Id(10L))
                .thenReturn(List.of(voo));

        List<Voo> encontrados = vooService.buscarPorCompanhia(10L);

        assertEquals(1, encontrados.size());
    }

    // ------------------------------------------------------------
    // 5 - ATUALIZAR VOO
    // ------------------------------------------------------------

    /*
     * ============================
     * TESTE: Atualizar voo com sucesso
     * OBJETIVO: Permitir atualização completa do voo.
     * CONDIÇÕES: Voo existente.
     * RESULTADO ESPERADO: Alterações refletidas no retorno.
     * ============================
     */
    @Test
    void deveAtualizarVooComSucesso() {
        Voo atualizado = new Voo();
        atualizado.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(10));
        atualizado.setHorarioChegadaPrevisto(LocalDateTime.now().plusHours(12));
        atualizado.setStatus(VooStatus.CONCLUIDO);

        when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
        when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

        Voo retorno = vooService.atualizarVoo(5L, atualizado);

        assertEquals(VooStatus.CONCLUIDO, retorno.getStatus());
        assertEquals(atualizado.getHorarioPartidaPrevisto(), retorno.getHorarioPartidaPrevisto());
    }

    /*
     * ============================
     * TESTE: Erro ao atualizar voo inexistente
     * OBJETIVO: Garantir que voo precisa existir para atualizar.
     * CONDIÇÕES: findById retornando empty.
     * RESULTADO ESPERADO: Exceção "Voo não encontrado".
     * ============================
     */
    @Test
    void deveLancarErroAtualizarVooNaoEncontrado() {
        when(vooRepository.findById(5L)).thenReturn(Optional.empty());

        RuntimeException e = assertThrows(RuntimeException.class, () -> vooService.atualizarVoo(5L, voo));
        assertEquals("Voo não encontrado", e.getMessage());
    }
}
