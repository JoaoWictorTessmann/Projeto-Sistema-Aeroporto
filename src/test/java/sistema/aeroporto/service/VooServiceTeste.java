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

import sistema.aeroporto.dto.CompanhiaAereaDTO;
import sistema.aeroporto.dto.PilotoDTO;
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

public class VooServiceTeste {

        @InjectMocks
        private VooService vooService;

        @Mock
        private VooRepository vooRepository;

        @Mock
        private PilotoRepository pilotoRepository;

        @Mock
        private CompanhiaAereaRepository companhiaAereaRepository;

        // -------------------------------------------------------------------------
        // Objetos reutilizados nos testes
        // -------------------------------------------------------------------------

        private Piloto pilotoAtivo;
        private Piloto pilotoInativo;
        private CompanhiaAerea companhiaAtiva;
        private VooDTO vooDTO;

        private static final String CPF = "11144477735";
        private static final String CNPJ = "05.451.308/0001-77";
        private static final String CODIGO_VOO = "VOO001";

        @BeforeEach
        void setup() {
                MockitoAnnotations.openMocks(this);

                // --- Entidades retornadas pelos mocks de repositório ---
                pilotoAtivo = new Piloto();
                pilotoAtivo.setId(1L);
                pilotoAtivo.setNome("João");
                pilotoAtivo.setCpf(CPF);
                pilotoAtivo.setStatus(PilotoStatus.ATIVO);

                pilotoInativo = new Piloto();
                pilotoInativo.setId(2L);
                pilotoInativo.setNome("Maria");
                pilotoInativo.setCpf(CPF);
                pilotoInativo.setStatus(PilotoStatus.INATIVO);

                companhiaAtiva = new CompanhiaAerea();
                companhiaAtiva.setId(10L);
                companhiaAtiva.setNome("Azul");
                companhiaAtiva.setCnpj(CNPJ);
                companhiaAtiva.setStatus(CompanhiaAereaStatus.ATIVA);

                // --- DTO usado para criar voo ---
                // PilotoDTO e CompanhiaAereaDTO aninhados com CPF/CNPJ que o service usa para
                // buscar
                PilotoDTO pilotoDTO = new PilotoDTO(
                                "1", "João", "30", "M", CPF, "PPL", null, PilotoStatus.ATIVO.name());
                CompanhiaAereaDTO companhiaDTO = new CompanhiaAereaDTO(
                                "10", "Azul", CNPJ, true, CompanhiaAereaStatus.ATIVA.name());

                vooDTO = new VooDTO(
                                null,
                                pilotoDTO,
                                companhiaDTO,
                                CODIGO_VOO,
                                "São Paulo",
                                "Rio de Janeiro",
                                null,
                                LocalDateTime.now().plusHours(2).toString(),
                                LocalDateTime.now().plusHours(4).toString(),
                                null,
                                null,
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

                // VooService.criarVoo busca piloto por CPF e companhia por CNPJ
                when(pilotoRepository.findByCpf(CPF)).thenReturn(Optional.of(pilotoAtivo));
                when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
                when(companhiaAereaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(companhiaAtiva));
                when(vooRepository.existsByCodigo(CODIGO_VOO)).thenReturn(false);
                when(vooRepository.save(any(Voo.class))).thenReturn(vooSalvo);

                Voo criado = vooService.criarVoo(vooDTO);

                assertNotNull(criado);
                assertEquals(CODIGO_VOO, criado.getCodigo());
                assertEquals(VooStatus.AGENDADO, criado.getStatus());
        }

        @Test
        void deveLancarErroQuandoPilotoNaoExiste() {

                when(pilotoRepository.findByCpf(CPF)).thenReturn(Optional.empty());

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTO));

                assertEquals("Piloto não encontrado", e.getMessage());
        }

        @Test
        void deveLancarErroQuandoOrigemIgualDestino() {

                VooDTO vooDTOInvalido = new VooDTO(
                                null,
                                vooDTO.piloto(),
                                vooDTO.companhia(),
                                CODIGO_VOO,
                                "Rio de Janeiro", // origem == destino
                                "Rio de Janeiro",
                                null,
                                LocalDateTime.now().plusHours(2).toString(),
                                null, null, null, null);

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTOInvalido));

                assertEquals("Origem e destino não podem ser iguais", e.getMessage());
        }

        @Test
        void deveLancarErroQuandoHorarioNoPassado() {

                VooDTO vooDTOInvalido = new VooDTO(
                                null,
                                vooDTO.piloto(),
                                vooDTO.companhia(),
                                CODIGO_VOO,
                                "São Paulo",
                                "Rio de Janeiro",
                                null,
                                LocalDateTime.now().minusHours(1).toString(), // passado
                                null, null, null, null);

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTOInvalido));

                assertEquals("Horário de partida não pode ser no passado", e.getMessage());
        }

        @Test
        void deveLancarErroConflitoDeHorario() {

                Voo vooExistente = new Voo();
                vooExistente.setHorarioPartidaPrevisto(
                                LocalDateTime.parse(vooDTO.horarioPartidaPrevisto()));

                when(pilotoRepository.findByCpf(CPF)).thenReturn(Optional.of(pilotoAtivo));
                when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of(vooExistente));

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTO));

                assertEquals("Piloto já está escalado para outro voo nesse horário", e.getMessage());
        }

        @Test
        void deveLancarErroQuandoCompanhiaNaoExiste() {

                when(pilotoRepository.findByCpf(CPF)).thenReturn(Optional.of(pilotoAtivo));
                when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
                when(companhiaAereaRepository.findByCnpj(CNPJ)).thenReturn(Optional.empty());

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTO));

                assertEquals("Companhia aérea não encontrada", e.getMessage());
        }

        @Test
        void deveLancarErroQuandoCompanhiaInativa() {

                CompanhiaAerea companhiaInativa = new CompanhiaAerea();
                companhiaInativa.setId(10L);
                companhiaInativa.setCnpj(CNPJ);
                companhiaInativa.setStatus(CompanhiaAereaStatus.INATIVA);

                when(pilotoRepository.findByCpf(CPF)).thenReturn(Optional.of(pilotoAtivo));
                when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
                when(companhiaAereaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(companhiaInativa));

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTO));

                assertEquals("Companhia não está ativa", e.getMessage());
        }

        @Test
        void deveLancarErroCodigoDuplicado() {

                when(pilotoRepository.findByCpf(CPF)).thenReturn(Optional.of(pilotoAtivo));
                when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of());
                when(companhiaAereaRepository.findByCnpj(CNPJ)).thenReturn(Optional.of(companhiaAtiva));
                when(vooRepository.existsByCodigo(CODIGO_VOO)).thenReturn(true);

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.criarVoo(vooDTO));

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

                when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
                when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

                Voo iniciado = vooService.iniciarVoo(5L);

                assertEquals(VooStatus.EM_VOO, iniciado.getStatus());
                assertNotNull(iniciado.getHorarioPartidaReal());
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
                voo.setPiloto(pilotoInativo); // piloto INATIVO

                when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.iniciarVoo(5L));

                assertEquals("Piloto não pode iniciar o voo", e.getMessage());
        }

        @Test
        void deveLancarErroIniciarVooNaoAgendado() {

                Voo voo = new Voo();
                voo.setId(5L);
                voo.setStatus(VooStatus.CANCELADO); // não é AGENDADO
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

                VooDTO motivoDTO = new VooDTO(
                                null, null, null, null, null, null,
                                "Mau tempo",
                                null, null, null, null, null);

                when(vooRepository.findById(5L)).thenReturn(Optional.of(voo));
                when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

                Voo cancelado = vooService.cancelarVoo(5L, motivoDTO);

                assertEquals(VooStatus.CANCELADO, cancelado.getStatus());
                assertEquals("Mau tempo", cancelado.getMotivoCancelamento());
        }

        @Test
        void deveLancarErroSemMotivoCancelamento() {

                VooDTO motivoVazio = new VooDTO(
                                null, null, null, null, null, null,
                                "",
                                null, null, null, null, null);

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.cancelarVoo(5L, motivoVazio));

                assertEquals("Motivo do cancelamento é obrigatório", e.getMessage());
        }

        @Test
        void deveLancarErroCancelamentoVooNaoEncontrado() {

                VooDTO motivoDTO = new VooDTO(
                                null, null, null, null, null, null,
                                "Motivo qualquer",
                                null, null, null, null, null);

                when(vooRepository.findById(99L)).thenReturn(Optional.empty());

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.cancelarVoo(99L, motivoDTO));

                assertEquals("Voo não encontrado", e.getMessage());
        }

        // =========================================================================
        // listarTodos / buscarPorStatus / buscarPorPiloto / buscarPorCompanhia
        // =========================================================================

        @Test
        void deveListarTodosVoos() {

                when(vooRepository.findAll()).thenReturn(List.of(new Voo(), new Voo()));

                List<Voo> lista = vooService.listarTodos();

                assertEquals(2, lista.size());
        }

        @Test
        void deveBuscarPorStatus() {

                Voo voo = new Voo();
                voo.setStatus(VooStatus.AGENDADO);

                when(vooRepository.findByStatus(VooStatus.AGENDADO)).thenReturn(List.of(voo));

                VooDTO statusDTO = new VooDTO(
                                null, null, null, null, null, null,
                                null, null, null, null, null, "AGENDADO");

                List<Voo> encontrados = vooService.buscarPorStatus(statusDTO);

                assertEquals(1, encontrados.size());
                assertEquals(VooStatus.AGENDADO, encontrados.get(0).getStatus());
        }

        @Test
        void deveBuscarPorPiloto() {

                when(vooRepository.findByPiloto_Id(1L)).thenReturn(List.of(new Voo()));

                List<Voo> encontrados = vooService.buscarPorPiloto(1L);

                assertEquals(1, encontrados.size());
        }

        @Test
        void deveBuscarPorCompanhia() {

                when(vooRepository.findByCompanhia_Id(10L)).thenReturn(List.of(new Voo()));

                List<Voo> encontrados = vooService.buscarPorCompanhia(10L);

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

                VooDTO atualizadoDTO = new VooDTO(
                                null, null, null, null, null, null, null,
                                null, null,
                                LocalDateTime.now().plusHours(6).toString(),
                                LocalDateTime.now().plusHours(10).toString(),
                                "CONCLUIDO");

                when(vooRepository.findById(5L)).thenReturn(Optional.of(vooExistente));
                when(vooRepository.save(any(Voo.class))).thenAnswer(i -> i.getArgument(0));

                Voo retorno = vooService.atualizarVoo(5L, atualizadoDTO);

                assertEquals(VooStatus.CONCLUIDO, retorno.getStatus());
        }

        @Test
        void deveLancarErroAtualizarVooNaoEncontrado() {

                VooDTO atualizadoDTO = new VooDTO(
                                null, null, null, null, null, null, null,
                                null, null,
                                LocalDateTime.now().plusHours(1).toString(),
                                LocalDateTime.now().plusHours(5).toString(),
                                "CONCLUIDO");

                when(vooRepository.findById(99L)).thenReturn(Optional.empty());

                RuntimeException e = assertThrows(RuntimeException.class,
                                () -> vooService.atualizarVoo(99L, atualizadoDTO));

                assertEquals("Voo não encontrado", e.getMessage());
        }
}