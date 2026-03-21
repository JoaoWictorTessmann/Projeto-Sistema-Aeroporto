package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import sistema.aeroporto.dto.request.CompanhiaAereaRequest;
import sistema.aeroporto.dto.request.PilotoRequest;
import sistema.aeroporto.dto.request.VooRequest;
import sistema.aeroporto.dto.request.VooUpdateRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.dto.response.PilotoResponse;
import sistema.aeroporto.dto.response.VooResponse;
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

        private PilotoResponse criarPilotoAtivo(String nome, String cpf) {
                return pilotoService.salvarPiloto(
                                new PilotoRequest(nome, 30, "M", cpf, null, "PPL", "ATIVO"));
        }

        private PilotoResponse criarPilotoInativo(String nome, String cpf) {
                return pilotoService.salvarPiloto(
                                new PilotoRequest(nome, 30, "M", cpf, null, "PPL", "INATIVO"));
        }

        private CompanhiaAereaResponse criarCompanhiaAtiva(String nome, String cnpj) {
                return companhiaAereaService.salvarCompanhia(
                                new CompanhiaAereaRequest(nome, cnpj, null, true, "ATIVA"));
        }

        private CompanhiaAereaResponse criarCompanhiaInativa(String nome, String cnpj) {
                return companhiaAereaService.salvarCompanhia(
                                new CompanhiaAereaRequest(nome, cnpj, null, true, "INATIVA"));
        }

        private VooRequest montarVooRequest(Long pilotoId, Long companhiaId,
                        String codigo, String origem, String destino, LocalDateTime horario) {
                return new VooRequest(pilotoId, companhiaId, codigo, origem, destino,
                                horario, horario.plusHours(4));
        }

        @Test
        @DisplayName("Deve criar um voo com sucesso")
        void deveCriarVooComSucesso() {
                var piloto = criarPilotoAtivo("João", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

                VooResponse voo = vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "AZ1234", "GRU", "JFK", LocalDateTime.now().plusHours(2)));

                assertEquals("AZ1234", voo.codigo());
        }

        @Test
        @DisplayName("Deve lançar erro quando piloto não existe")
        void deveLancarErroQuandoPilotoNaoExiste() {
                var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

                VooRequest request = montarVooRequest(999L, companhia.id(),
                                "AZ1234", "GRU", "JFK", LocalDateTime.now().plusHours(2));

                RuntimeException exception = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(request));

                assertEquals("Piloto não encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar erro por conflito de horário")
        void deveLancarErroPorConflitoDeHorario() {
                var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Gol", "05.451.308/0001-77");
                LocalDateTime horario = LocalDateTime.now().plusHours(3);

                vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(), "GL1001", "GRU", "REC", horario));

                RuntimeException exception = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(montarVooRequest(
                                                piloto.id(), companhia.id(), "GL2002", "GRU", "SSA", horario)));

                assertEquals("Piloto já está escalado para outro voo nesse horário", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar erro quando companhia não existe")
        void deveLancarErroQuandoCompanhiaNaoExiste() {
                var piloto = criarPilotoAtivo("João", "111.444.777-35");

                VooRequest request = montarVooRequest(piloto.id(), 999L,
                                "AZ9000", "GRU", "MIA", LocalDateTime.now().plusHours(2));

                RuntimeException exception = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(request));

                assertEquals("Companhia não encontrada", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar erro quando companhia está inativa")
        void deveLancarErroQuandoCompanhiaInativa() {
                var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
                var companhia = criarCompanhiaInativa("Latam", "05.451.308/0001-77");

                VooRequest request = montarVooRequest(piloto.id(), companhia.id(),
                                "LA8000", "GRU", "SCL", LocalDateTime.now().plusHours(4));

                RuntimeException exception = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(request));

                assertEquals("Companhia não está ativa", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar erro quando código for duplicado")
        void deveLancarErroCodigoDuplicado() {
                var piloto = criarPilotoAtivo("João", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

                vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "VOO001", "GRU", "JFK", LocalDateTime.now().plusHours(2)));

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                                "VOO001", "GRU", "LAX", LocalDateTime.now().plusHours(4))));

                assertEquals("Código de voo já existente", e.getMessage());
        }

        @Test
        @DisplayName("Deve lançar erro quando origem e destino forem iguais")
        void deveLancarErroOrigemIgualDestino() {
                var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

                VooRequest request = montarVooRequest(piloto.id(), companhia.id(),
                                "VOO100", "RIO", "RIO", LocalDateTime.now().plusHours(2));

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(request));

                assertEquals("Origem e destino não podem ser iguais", e.getMessage());
        }

        @Test
        @DisplayName("Deve lançar erro quando horário estiver no passado")
        void deveLancarErroHorarioNoPassado() {
                var piloto = criarPilotoAtivo("Pedro", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

                VooRequest request = montarVooRequest(piloto.id(), companhia.id(),
                                "VOO200", "GRU", "SSA", LocalDateTime.now().minusHours(1));

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(request));

                assertEquals("Horário de partida não pode ser no passado", e.getMessage());
        }

        @Test
        @DisplayName("Deve iniciar voo com sucesso")
        void deveIniciarVooComSucesso() {
                var piloto = criarPilotoAtivo("Maria", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Gol", "05.451.308/0001-77");

                VooResponse voo = vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "VOO300", "GRU", "POA", LocalDateTime.now().plusHours(2)));

                VooResponse iniciado = vooService.iniciarVoo(voo.id());

                assertEquals(VooStatus.VOANDO.name(), iniciado.status());
                assertNotNull(iniciado.horarioPartidaReal());
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

                VooResponse voo = vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "VOO400", "GRU", "MIA", LocalDateTime.now().plusHours(3)));

                RuntimeException exception = assertThrows(RuntimeException.class,
                                () -> vooService.iniciarVoo(voo.id()));

                assertEquals("Piloto não pode iniciar o voo", exception.getMessage());
        }

        @Test
        @DisplayName("Deve cancelar voo com sucesso")
        void deveCancelarVooComSucesso() {
                var piloto = criarPilotoAtivo("João", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("LATAM", "05.451.308/0001-77");

                VooResponse voo = vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "VOO500", "GRU", "GIG", LocalDateTime.now().plusHours(2)));

                VooResponse cancelado = vooService.cancelarVoo(voo.id(), "Mau tempo");

                assertEquals(VooStatus.CANCELADO.name(), cancelado.status());
                assertEquals("Mau tempo", cancelado.motivoCancelamento());
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
                List<VooResponse> lista = vooService.listarTodos();
                assertNotNull(lista);
        }

        @Test
        @DisplayName("Deve buscar por status")
        void deveBuscarPorStatus() {
                List<VooResponse> encontrados = vooService.buscarPorStatus("AGENDADO");
                assertNotNull(encontrados);
        }

        @Test
        @DisplayName("Deve buscar voos por piloto")
        void deveBuscarPorPiloto() {
                var piloto = criarPilotoAtivo("João", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Azul", "05.451.308/0001-77");

                vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "BUS001", "GRU", "SSA", LocalDateTime.now().plusHours(3)));

                List<VooResponse> encontrados = vooService.buscarPorPiloto(piloto.id());

                assertEquals(1, encontrados.size());
        }

        @Test
        @DisplayName("Deve buscar voos por companhia")
        void deveBuscarPorCompanhia() {
                var piloto = criarPilotoAtivo("Carlos", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("Gol", "05.451.308/0001-77");

                vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "BUS002", "GRU", "POA", LocalDateTime.now().plusHours(4)));

                List<VooResponse> encontrados = vooService.buscarPorCompanhia(companhia.id());

                assertEquals(1, encontrados.size());
        }

        @Test
        @DisplayName("Deve atualizar voo com sucesso")
        void deveAtualizarVooComSucesso() {
                var piloto = criarPilotoAtivo("João", "111.444.777-35");
                var companhia = criarCompanhiaAtiva("LATAM", "05.451.308/0001-77");

                VooResponse voo = vooService.criarVoo(montarVooRequest(piloto.id(), companhia.id(),
                                "VOO600", "GRU", "GIG", LocalDateTime.now().plusHours(5)));

                VooUpdateRequest atualizacao = new VooUpdateRequest(
                                LocalDateTime.now().plusHours(6),
                                LocalDateTime.now().plusHours(10),
                                "CONCLUIDO");

                VooResponse retorno = vooService.atualizarVoo(voo.id(), atualizacao);

                assertEquals(VooStatus.CONCLUIDO.name(), retorno.status());
        }

        @Test
        @DisplayName("Deve lançar erro ao atualizar voo inexistente")
        void deveLancarErroAtualizarVooNaoEncontrado() {
                VooUpdateRequest request = new VooUpdateRequest(
                                LocalDateTime.now().plusHours(1),
                                LocalDateTime.now().plusHours(5),
                                "CONCLUIDO");

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.atualizarVoo(999L, request));

                assertEquals("Voo não encontrado", e.getMessage());
        }
}