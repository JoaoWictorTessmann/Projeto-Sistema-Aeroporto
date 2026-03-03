package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.Voo;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.model.enums.PilotoStatus;
import sistema.aeroporto.model.enums.VooStatus;

@SpringBootTest
@Transactional
public class VooServiceIntegrationTest {

    @Autowired
    private PilotoService pilotoService;

    @Autowired
    private CompanhiaAereaService companhiaAereaService;

    @Autowired
    private VooService vooService;

    @Test
    @DisplayName("Deve criar um voo com sucesso")
    void deveCriarVooComSucesso() {

        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("11144477735");
        piloto.setStatus(PilotoStatus.ATIVO);

        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Azul");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);

        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("AZ1234");
        voo.setOrigem("GRU");
        voo.setDestino("JFK");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));

        Voo vooSalvo = vooService.criarVoo(voo);

        Voo vooCriado = vooService.buscarPorId(vooSalvo.getId());

        assertEquals("AZ1234", vooCriado.getCodigo());
    }

    @Test
    @DisplayName("Deve lançar erro quando piloto não existe")
    void deveLancarErroQuandoPilotoNaoExiste() {

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Azul");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);

        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(null);
        voo.setCompanhia(companhia);
        voo.setCodigo("AZ1234");
        voo.setOrigem("GRU");
        voo.setDestino("JFK");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(voo));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro ao iniciar voo com piloto inativo")
    void deveLancarErroQuandoPilotoInativo() {

        Piloto piloto = new Piloto();
        piloto.setNome("Maria");
        piloto.setCpf("11144477735");
        piloto.setStatus(PilotoStatus.INATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Latam");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("LT5678");
        voo.setOrigem("GRU");
        voo.setDestino("LAX");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));

        Voo vooCriado = vooService.criarVoo(voo);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.iniciarVoo(vooCriado.getId()));

        assertEquals("Piloto não pode iniciar o voo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro por conflito de horário")
    void deveLancarErroPorConflitoDeHorario() {

        // arrange
        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("11144477735");
        piloto.setStatus(PilotoStatus.ATIVO);

        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Gol");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);

        companhia = companhiaAereaService.salvarCompanhia(companhia);

        LocalDateTime horario = LocalDateTime.now().plusHours(3);

        // Primeiro voo (válido)
        Voo voo1 = new Voo();
        voo1.setPiloto(piloto);
        voo1.setCompanhia(companhia);
        voo1.setCodigo("GL1001");
        voo1.setOrigem("GRU");
        voo1.setDestino("REC");
        voo1.setHorarioPartidaPrevisto(horario);

        vooService.criarVoo(voo1);

        // Segundo voo com mesmo piloto e mesmo horário
        Voo voo2 = new Voo();
        voo2.setPiloto(piloto);
        voo2.setCompanhia(companhia);
        voo2.setCodigo("GL2002");
        voo2.setOrigem("GRU");
        voo2.setDestino("SSA");
        voo2.setHorarioPartidaPrevisto(horario);

        // act & assert

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(voo2));

        assertEquals("Piloto já está escalado para outro voo nesse horário", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando companhia não existe")
    void deveLancarErroQuandoCompanhiaNaoExiste() {

        // ---------- Arrange ----------

        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhiaFake = new CompanhiaAerea();
        companhiaFake.setId(999L); // ID que não existe no banco

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhiaFake);
        voo.setCodigo("AZ9000");
        voo.setOrigem("GRU");
        voo.setDestino("MIA");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));

        // ---------- Act + Assert ----------

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(voo));

        assertEquals("Companhia aérea não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando companhia está inativa")
    void deveLancarErroQuandoCompanhiaInativa() {

        // ---------- Arrange ----------

        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Latam");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.INATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("LA8000");
        voo.setOrigem("GRU");
        voo.setDestino("SCL");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(4));

        // ---------- Act + Assert ----------

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(voo));

        assertEquals("Companhia não está ativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando código for duplicado")
    void deveLancarErroCodigoDuplicado() {

        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Azul");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo1 = new Voo();
        voo1.setPiloto(piloto);
        voo1.setCompanhia(companhia);
        voo1.setCodigo("VOO001");
        voo1.setOrigem("GRU");
        voo1.setDestino("JFK");
        voo1.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));
        vooService.criarVoo(voo1);

        Voo voo2 = new Voo();
        voo2.setPiloto(piloto);
        voo2.setCompanhia(companhia);
        voo2.setCodigo("VOO001");
        voo2.setOrigem("GRU");
        voo2.setDestino("LAX");
        voo2.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(4));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(voo2));

        assertEquals("Código de voo já existente", e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando origem e destino forem iguais")
    void deveLancarErroOrigemIgualDestino() {

        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCodigo("VOO100");
        voo.setOrigem("RIO");
        voo.setDestino("RIO");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(voo));

        assertEquals("Origem e destino não podem ser iguais", e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando horário estiver no passado")
    void deveLancarErroHorarioNoPassado() {

        Piloto piloto = new Piloto();
        piloto.setNome("Pedro");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCodigo("VOO200");
        voo.setOrigem("GRU");
        voo.setDestino("SSA");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().minusHours(1));

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(voo));

        assertEquals("Horário de partida não pode ser no passado", e.getMessage());
    }

    @Test
    @DisplayName("Deve iniciar voo com sucesso")
    void deveIniciarVooComSucesso() {

        Piloto piloto = new Piloto();
        piloto.setNome("Maria");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Gol");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("VOO300");
        voo.setOrigem("GRU");
        voo.setDestino("POA");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));
        voo = vooService.criarVoo(voo);

        Voo iniciado = vooService.iniciarVoo(voo.getId());

        assertEquals(VooStatus.EM_VOO, iniciado.getStatus());
        assertEquals(true, iniciado.getHorarioPartidaReal() != null);
    }

    @Test
    @DisplayName("Deve lançar erro ao iniciar voo inexistente")
    void deveLancarErroAoIniciarVooNaoEncontrado() {

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.iniciarVoo(999L));

        assertEquals("Voo não encontrado", e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro ao iniciar voo com piloto inativo")
    void deveLancarErroPilotoInativoAoIniciar() {

        // Criar piloto INATIVO
        Piloto piloto = new Piloto();
        piloto.setNome("Lucas");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.INATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        // Criar companhia ATIVA
        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Latam");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        // Criar voo válido
        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("VOO400");
        voo.setOrigem("GRU");
        voo.setDestino("MIA");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(3));

        Voo vooCriado = vooService.criarVoo(voo);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vooService.iniciarVoo(vooCriado.getId()));

        assertEquals("Piloto não pode iniciar o voo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar voo com sucesso")
    void deveCancelarVooComSucesso() {

        // Criar piloto
        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("11144477735");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        // Criar companhia
        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("LATAM");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        // Criar voo
        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("VOO500");
        voo.setOrigem("GRU");
        voo.setDestino("GIG");
        voo.setStatus(VooStatus.AGENDADO);
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(2));

        voo = vooService.criarVoo(voo);

        // Executar cancelamento
        Voo cancelado = vooService.cancelarVoo(voo.getId(), "Mau tempo");

        // Validações
        assertEquals(VooStatus.CANCELADO, cancelado.getStatus());
        assertEquals("Mau tempo", cancelado.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Deve lançar erro sem motivo de cancelamento")
    void deveLancarErroSemMotivoCancelamento() {

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.cancelarVoo(1L, ""));

        assertEquals("Motivo do cancelamento é obrigatório", e.getMessage());
    }

    @Test
    @DisplayName("Deve listar todos os voos")
    void deveListarTodosVoos() {

        List<Voo> lista = vooService.listarTodos();

        assertEquals(true, lista.size() >= 0);
    }

    @Test
    @DisplayName("Deve buscar por status")
    void deveBuscarPorStatus() {

        List<Voo> encontrados = vooService.buscarPorStatus(VooStatus.AGENDADO);

        assertEquals(true, encontrados != null);
    }

    @Test
    @DisplayName("Deve buscar voos por piloto")
    void deveBuscarPorPiloto() {

        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Azul");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("BUS001");
        voo.setOrigem("GRU");
        voo.setDestino("SSA");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(3));
        vooService.criarVoo(voo);

        List<Voo> encontrados = vooService.buscarPorPiloto(piloto.getId());

        assertEquals(1, encontrados.size());
    }

    @Test
    @DisplayName("Deve buscar voos por companhia")
    void deveBuscarPorCompanhia() {

        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("111.444.777-35");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("Gol");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        Voo voo = new Voo();
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);
        voo.setCodigo("BUS002");
        voo.setOrigem("GRU");
        voo.setDestino("POA");
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(4));
        vooService.criarVoo(voo);

        List<Voo> encontrados = vooService.buscarPorCompanhia(companhia.getId());

        assertEquals(1, encontrados.size());
    }

    @Test
    @DisplayName("Deve atualizar voo com sucesso")
    void deveAtualizarVooComSucesso() {

        // Criar e salvar piloto
        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("11144477735");
        piloto.setStatus(PilotoStatus.ATIVO);
        piloto = pilotoService.salvarPiloto(piloto);

        // Criar e salvar companhia
        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome("LATAM");
        companhia.setCnpj("05.451.308/0001-77");
        companhia.setStatus(CompanhiaAereaStatus.ATIVA);
        companhia = companhiaAereaService.salvarCompanhia(companhia);

        // Criar voo inicial
        Voo voo = new Voo();
        voo.setCodigo("VOO500");
        voo.setOrigem("GRU");
        voo.setDestino("GIG");
        voo.setStatus(VooStatus.AGENDADO);
        voo.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(5));
        voo.setPiloto(piloto);
        voo.setCompanhia(companhia);

        voo = vooService.criarVoo(voo);

        // Criar objeto com dados atualizados
        Voo atualizado = new Voo();
        atualizado.setStatus(VooStatus.CONCLUIDO);
        atualizado.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(10));

        // Atualizar
        Voo retorno = vooService.atualizarVoo(voo.getId(), atualizado);

        assertEquals(VooStatus.CONCLUIDO, retorno.getStatus());
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar voo inexistente")
    void deveLancarErroAtualizarVooNaoEncontrado() {

        Voo atualizado = new Voo();
        atualizado.setStatus(VooStatus.CONCLUIDO);
        atualizado.setHorarioPartidaPrevisto(LocalDateTime.now().plusHours(5));

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> vooService.atualizarVoo(999L, atualizado));

        assertEquals("Voo não encontrado", e.getMessage());
    }
}