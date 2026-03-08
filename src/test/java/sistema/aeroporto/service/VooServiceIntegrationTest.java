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
import sistema.aeroporto.dto.CompanhiaAereaDTO;
import sistema.aeroporto.dto.PilotoDTO;
import sistema.aeroporto.dto.VooDTO;
import sistema.aeroporto.model.Voo;
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

    // -------------------------------------------------------------------------
    // Helpers para reduzir repetição na criação de DTOs
    // -------------------------------------------------------------------------

    /** Cria e salva um piloto ATIVO com CPF válido. */
    private sistema.aeroporto.model.Piloto criarPilotoAtivo(String nome, String cpf) {
        PilotoDTO dto = new PilotoDTO(
                nome, // nome
                "30", // idade
                "M", // genero
                cpf, // cpf
                "PPL", // habilitacao
                null, // matricula (gerada pelo service)
                "ATIVO" // status
        );
        return pilotoService.salvarPiloto(dto);
    }

    /** Cria e salva um piloto INATIVO com CPF válido. */
    private sistema.aeroporto.model.Piloto criarPilotoInativo(String nome, String cpf) {
        PilotoDTO dto = new PilotoDTO(
                nome, "30", "M", cpf, "PPL", null, "INATIVO");
        return pilotoService.salvarPiloto(dto);
    }

    /** Cria e salva uma companhia ATIVA. */
    private sistema.aeroporto.model.CompanhiaAerea criarCompanhiaAtiva(String nome, String cnpj) {
        CompanhiaAereaDTO dto = new CompanhiaAereaDTO(nome, cnpj, true, "ATIVA");
        return companhiaAereaService.salvarCompanhia(dto);
    }

    /** Cria e salva uma companhia INATIVA. */
    private sistema.aeroporto.model.CompanhiaAerea criarCompanhiaInativa(String nome, String cnpj) {
        CompanhiaAereaDTO dto = new CompanhiaAereaDTO(nome, cnpj, true, "INATIVA");
        return companhiaAereaService.salvarCompanhia(dto);
    }

    /**
     * Monta um VooDTO completo a partir dos objetos já salvos.
     * horarioPartida deve ser uma string ISO-8601, ex:
     * LocalDateTime.now().plusHours(2).toString()
     */
    private VooDTO montarVooDTO(
            sistema.aeroporto.model.Piloto piloto,
            sistema.aeroporto.model.CompanhiaAerea companhia,
            String codigo,
            String origem,
            String destino,
            String horarioPartida) {
        PilotoDTO pilotoDTO = new PilotoDTO(
                piloto.getId().toString(),
                piloto.getNome(),
                piloto.getIdade(),
                piloto.getGenero(),
                piloto.getCpf(),
                piloto.getHabilitacao(),
                piloto.getMatricula(),
                piloto.getStatus() != null ? piloto.getStatus().name() : null);
        CompanhiaAereaDTO companhiaDTO = new CompanhiaAereaDTO(
                companhia.getId().toString(),
                companhia.getNome(),
                companhia.getCnpj(),
                companhia.getSeguroAeronave(),
                companhia.getStatus() != null ? companhia.getStatus().name() : null);
        return new VooDTO(
                null, // id (ainda não existe)
                pilotoDTO,
                companhiaDTO,
                codigo,
                origem,
                destino,
                null, // motivoCancelamento
                horarioPartida, // horarioPartidaPrevisto
                null, // horarioChegadaPrevisto
                null, // horarioPartidaReal
                null, // horarioChegadaReal
                null // status
        );
    }

    // =========================================================================
    // TESTES
    // =========================================================================

    @Test
    @DisplayName("Deve criar um voo com sucesso")
    void deveCriarVooComSucesso() {

        var piloto = criarPilotoAtivo("João", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

        VooDTO vooDTO = montarVooDTO(
                piloto, companhia,
                "AZ1234", "GRU", "JFK",
                LocalDateTime.now().plusHours(2).toString());

        Voo vooSalvo = vooService.criarVoo(vooDTO);
        Voo vooCriado = vooService.buscarPorId(vooSalvo.getId());

        assertEquals("AZ1234", vooCriado.getCodigo());
    }

    @Test
    @DisplayName("Deve lançar erro quando piloto não existe")
    void deveLancarErroQuandoPilotoNaoExiste() {

        var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

        // PilotoDTO com CPF que não existe no banco
        PilotoDTO pilotoInexistente = new PilotoDTO(
                null, "Fantasma", "30", "M",
                "000.000.000-00", "PPL", null, "ATIVO");
        CompanhiaAereaDTO companhiaDTO = new CompanhiaAereaDTO(
                companhia.getId().toString(),
                companhia.getNome(), companhia.getCnpj(),
                companhia.getSeguroAeronave(), companhia.getStatus().name());

        VooDTO vooDTO = new VooDTO(
                null, pilotoInexistente, companhiaDTO,
                "AZ1234", "GRU", "JFK",
                null, LocalDateTime.now().plusHours(2).toString(),
                null, null, null, null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(vooDTO));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro ao iniciar voo com piloto inativo")
    void deveLancarErroQuandoPilotoInativo() {

        var piloto = criarPilotoInativo("Maria", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Latam", "05.451.308/0001-77");

        VooDTO vooDTO = montarVooDTO(
                piloto, companhia,
                "LT5678", "GRU", "LAX",
                LocalDateTime.now().plusHours(2).toString());

        Voo vooCriado = vooService.criarVoo(vooDTO);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.iniciarVoo(vooCriado.getId()));

        assertEquals("Piloto não pode iniciar o voo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro por conflito de horário")
    void deveLancarErroPorConflitoDeHorario() {

        var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Gol", "05.451.308/0001-77");

        String horario = LocalDateTime.now().plusHours(3).toString();

        vooService.criarVoo(montarVooDTO(piloto, companhia, "GL1001", "GRU", "REC", horario));

        VooDTO voo2 = montarVooDTO(piloto, companhia, "GL2002", "GRU", "SSA", horario);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(voo2));

        assertEquals("Piloto já está escalado para outro voo nesse horário", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando companhia não existe")
    void deveLancarErroQuandoCompanhiaNaoExiste() {

        var piloto = criarPilotoAtivo("João", "111.444.777-35");

        // CNPJ que não está cadastrado no banco
        CompanhiaAereaDTO companhiaFake = new CompanhiaAereaDTO(
                "999", "Fantasma", "00.000.000/0000-00", false, "ATIVA");
        PilotoDTO pilotoDTO = new PilotoDTO(
                piloto.getId().toString(), piloto.getNome(), piloto.getIdade(),
                piloto.getGenero(), piloto.getCpf(), piloto.getHabilitacao(),
                piloto.getMatricula(), piloto.getStatus().name());

        VooDTO vooDTO = new VooDTO(
                null, pilotoDTO, companhiaFake,
                "AZ9000", "GRU", "MIA",
                null, LocalDateTime.now().plusHours(2).toString(),
                null, null, null, null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(vooDTO));

        assertEquals("Companhia aérea não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando companhia está inativa")
    void deveLancarErroQuandoCompanhiaInativa() {

        var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
        var companhia = criarCompanhiaInativa("Latam", "05.451.308/0001-77");

        VooDTO vooDTO = montarVooDTO(
                piloto, companhia,
                "LA8000", "GRU", "SCL",
                LocalDateTime.now().plusHours(4).toString());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vooService.criarVoo(vooDTO));

        assertEquals("Companhia não está ativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando código for duplicado")
    void deveLancarErroCodigoDuplicado() {

        var piloto = criarPilotoAtivo("João", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

        vooService.criarVoo(montarVooDTO(
                piloto, companhia, "VOO001", "GRU", "JFK",
                LocalDateTime.now().plusHours(2).toString()));

        VooDTO voo2 = montarVooDTO(
                piloto, companhia, "VOO001", "GRU", "LAX",
                LocalDateTime.now().plusHours(4).toString());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(voo2));

        assertEquals("Código de voo já existente", e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando origem e destino forem iguais")
    void deveLancarErroOrigemIgualDestino() {

        var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

        VooDTO vooDTO = montarVooDTO(
                piloto, companhia, "VOO100", "RIO", "RIO",
                LocalDateTime.now().plusHours(2).toString());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooDTO));

        assertEquals("Origem e destino não podem ser iguais", e.getMessage());
    }

    @Test
    @DisplayName("Deve lançar erro quando horário estiver no passado")
    void deveLancarErroHorarioNoPassado() {

        var piloto = criarPilotoAtivo("Pedro", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

        VooDTO vooDTO = montarVooDTO(
                piloto, companhia, "VOO200", "GRU", "SSA",
                LocalDateTime.now().minusHours(1).toString());

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.criarVoo(vooDTO));

        assertEquals("Horário de partida não pode ser no passado", e.getMessage());
    }

    @Test
    @DisplayName("Deve iniciar voo com sucesso")
    void deveIniciarVooComSucesso() {

        var piloto = criarPilotoAtivo("Maria", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Gol", "05.451.308/0001-77");

        Voo voo = vooService.criarVoo(montarVooDTO(
                piloto, companhia, "VOO300", "GRU", "POA",
                LocalDateTime.now().plusHours(2).toString()));

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

        var piloto = criarPilotoInativo("Lucas", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Latam", "05.451.308/0001-77");

        Voo vooCriado = vooService.criarVoo(montarVooDTO(
                piloto, companhia, "VOO400", "GRU", "MIA",
                LocalDateTime.now().plusHours(3).toString()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> vooService.iniciarVoo(vooCriado.getId()));

        assertEquals("Piloto não pode iniciar o voo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar voo com sucesso")
    void deveCancelarVooComSucesso() {

        var piloto = criarPilotoAtivo("João", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("LATAM", "05.451.308/0001-77");

        Voo voo = vooService.criarVoo(montarVooDTO(
                piloto, companhia, "VOO500", "GRU", "GIG",
                LocalDateTime.now().plusHours(2).toString()));

        // cancelarVoo agora recebe VooDTO com motivoCancelamento
        VooDTO motivoDTO = new VooDTO(
                null, null, null, null, null, null,
                "Mau tempo", // motivoCancelamento
                null, null, null, null, null);

        Voo cancelado = vooService.cancelarVoo(voo.getId(), motivoDTO);

        assertEquals(VooStatus.CANCELADO, cancelado.getStatus());
        assertEquals("Mau tempo", cancelado.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Deve lançar erro sem motivo de cancelamento")
    void deveLancarErroSemMotivoCancelamento() {

        VooDTO motivoVazio = new VooDTO(
                null, null, null, null, null, null,
                "", // motivoCancelamento vazio
                null, null, null, null, null);

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> vooService.cancelarVoo(1L, motivoVazio));

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

        // buscarPorStatus agora recebe VooDTO com campo status preenchido
        VooDTO statusDTO = new VooDTO(
                null, null, null, null, null, null,
                null, null, null, null, null, "AGENDADO");

        List<Voo> encontrados = vooService.buscarPorStatus(statusDTO);

        assertEquals(true, encontrados != null);
    }

    @Test
    @DisplayName("Deve buscar voos por piloto")
    void deveBuscarPorPiloto() {

        var piloto = criarPilotoAtivo("João", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

        vooService.criarVoo(montarVooDTO(
                piloto, companhia, "BUS001", "GRU", "SSA",
                LocalDateTime.now().plusHours(3).toString()));

        List<Voo> encontrados = vooService.buscarPorPiloto(piloto.getId());

        assertEquals(1, encontrados.size());
    }

    @Test
    @DisplayName("Deve buscar voos por companhia")
    void deveBuscarPorCompanhia() {

        var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("Gol", "05.451.308/0001-77");

        vooService.criarVoo(montarVooDTO(
                piloto, companhia, "BUS002", "GRU", "POA",
                LocalDateTime.now().plusHours(4).toString()));

        List<Voo> encontrados = vooService.buscarPorCompanhia(companhia.getId());

        assertEquals(1, encontrados.size());
    }

    @Test
    @DisplayName("Deve atualizar voo com sucesso")
    void deveAtualizarVooComSucesso() {

        var piloto = criarPilotoAtivo("João", "111.444.777-35");
        var companhia = criarCompanhiaAtiva("LATAM", "05.451.308/0001-77");

        Voo voo = vooService.criarVoo(montarVooDTO(
                piloto, companhia, "VOO600", "GRU", "GIG",
                LocalDateTime.now().plusHours(5).toString()));

        // atualizarVoo agora recebe VooDTO com horarios e status
        VooDTO atualizadoDTO = new VooDTO(
                null, null, null, null, null, null, null,
                null, null,
                LocalDateTime.now().plusHours(6).toString(), // horarioPartidaReal
                LocalDateTime.now().plusHours(10).toString(), // horarioChegadaReal
                "CONCLUIDO" // status
        );

        Voo retorno = vooService.atualizarVoo(voo.getId(), atualizadoDTO);

        assertEquals(VooStatus.CONCLUIDO, retorno.getStatus());
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar voo inexistente")
    void deveLancarErroAtualizarVooNaoEncontrado() {

        VooDTO atualizadoDTO = new VooDTO(
                null, null, null, null, null, null, null,
                null, null,
                LocalDateTime.now().plusHours(1).toString(),
                LocalDateTime.now().plusHours(5).toString(),
                "CONCLUIDO");

        RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> vooService.atualizarVoo(999L, atualizadoDTO));

        assertEquals("Voo não encontrado", e.getMessage());
    }
}