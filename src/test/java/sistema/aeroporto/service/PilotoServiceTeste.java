package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sistema.aeroporto.dto.request.PilotoRequest;
import sistema.aeroporto.dto.response.PilotoResponse;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.enums.PilotoStatus;
import sistema.aeroporto.repository.PilotoRepository;

public class PilotoServiceTeste {

    @Mock
    private PilotoRepository pilotoRepository;

    @InjectMocks
    private PilotoService pilotoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Piloto pilotoEntidade(Long id, String nome, String cpf) {
        Piloto p = new Piloto();
        p.setId(id);
        p.setNome(nome);
        p.setCpf(cpf);
        p.setStatus(PilotoStatus.ATIVO);
        return p;
    }

    @Test
    void deveListarTodosPilotos() {
        Piloto p = pilotoEntidade(1L, "João", "123");
        when(pilotoRepository.findAll()).thenReturn(Arrays.asList(p, p));

        List<PilotoResponse> resultado = pilotoService.listarTodosPilotos();

        assertEquals(2, resultado.size());
    }

    @Test
    void deveBuscarPorCpfExistente() {
        Piloto p = pilotoEntidade(1L, "João", "123");
        when(pilotoRepository.findByCpf("123")).thenReturn(Optional.of(p));

        PilotoResponse resultado = pilotoService.buscarPorCpf("123");

        assertNotNull(resultado);
        assertEquals("123", resultado.cpf());
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoExiste() {
        when(pilotoRepository.findByCpf("999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.buscarPorCpf("999"));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }

    @Test
    void deveSalvarPilotoGerandoMatricula() {
        PilotoRequest request = new PilotoRequest("Teste", 35, "1", "11144477735", null, "ATPL", null, "ATIVO");

        Piloto salvo = pilotoEntidade(10L, "Teste", "11144477735");
        salvo.setMatricula("PIL" + java.time.LocalDate.now().getYear() + "0010");

        when(pilotoRepository.save(any(Piloto.class))).thenReturn(salvo);

        PilotoResponse resultado = pilotoService.salvarPiloto(request);

        assertNotNull(resultado);
        assertTrue(resultado.matricula().startsWith("PIL"));
    }

    @Test
    void deveDeletarPiloto() {
        doNothing().when(pilotoRepository).deleteById(1L);

        pilotoService.deletarPiloto(1L);

        verify(pilotoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveAtualizarPiloto() {
        Piloto existente = pilotoEntidade(1L, "Carlos", "11144477735");

        PilotoRequest request = new PilotoRequest("Carlos", 40, "1", "11144477735", null, "ATPL", "MAT123", "ATIVO");

        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pilotoRepository.save(any(Piloto.class))).thenReturn(existente);

        PilotoResponse result = pilotoService.atualizarPiloto(1L, request);

        assertNotNull(result);
    }

    @Test
    void deveLancarExcecaoAoAtualizarPilotoInexistente() {
        PilotoRequest request = new PilotoRequest("Carlos", 40, "1", "11144477735", null, "ATPL", "MAT123", "ATIVO");

        when(pilotoRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.atualizarPiloto(2L, request));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }
}