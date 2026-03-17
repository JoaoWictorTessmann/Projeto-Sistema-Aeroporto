package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import sistema.aeroporto.dto.request.PilotoRequest;
import sistema.aeroporto.dto.response.PilotoResponse;

@SpringBootTest
@Transactional
public class PilotoServiceIntegrationTest {

    @Autowired
    private PilotoService pilotoService;

    @Test
    @DisplayName("Deve listar todos os pilotos do banco")
    void deveListarTodosPilotos() {
        PilotoRequest p1 = new PilotoRequest("João Silva", 35, "1", "549.909.720-82", null, "ATPL", "MAT123", "ATIVO");
        PilotoRequest p2 = new PilotoRequest("Maria Silva", 25, "2", "557.271.330-92", null, "ATPL2", "MAT125", "ATIVO");

        pilotoService.salvarPiloto(p1);
        pilotoService.salvarPiloto(p2);

        List<PilotoResponse> lista = pilotoService.listarTodosPilotos();

        assertEquals(2, lista.size());
        assertEquals("João Silva",  lista.get(0).nome());
        assertEquals("Maria Silva", lista.get(1).nome());
    }

    @Test
    @DisplayName("Deve buscar piloto por CPF existente")
    void deveBuscarPorCpfExistente() {
        PilotoRequest request = new PilotoRequest("João", 40, "1", "111.444.777-35", null, "ATPL", "MAT001", "ATIVO");
        pilotoService.salvarPiloto(request);

        PilotoResponse response = pilotoService.buscarPorCpf("11144477735");

        assertNotNull(response);
        assertEquals("João", response.nome());
        assertEquals("11144477735", response.cpf());
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar por CPF inexistente")
    void deveRetornarErroQuandoCpfNaoExiste() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.buscarPorCpf("99999999999"));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar piloto gerando matrícula automaticamente")
    void deveSalvarPilotoGerandoMatricula() {
        PilotoRequest request = new PilotoRequest("Carlos", 38, "1", "111.444.777-35", null, "ATPL", null, "ATIVO");

        PilotoResponse response = pilotoService.salvarPiloto(request);

        assertNotNull(response);
        assertEquals("Carlos", response.nome());
        assertEquals("11144477735", response.cpf());
        assertNotNull(response.matricula());
        assertFalse(response.matricula().isBlank());
        assertTrue(response.matricula().startsWith("PIL"));
    }

    @Test
    @DisplayName("Deve deletar piloto")
    void deveDeletarPiloto() {
        PilotoRequest request = new PilotoRequest("Carlos", 38, "1", "111.444.777-35", null, "ATPL", "MAT123", "ATIVO");
        PilotoResponse salvo = pilotoService.salvarPiloto(request);

        assertDoesNotThrow(() -> pilotoService.deletarPiloto(salvo.id()));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.buscarPorId(salvo.id()));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar piloto")
    void deveAtualizarPiloto() {
        PilotoRequest original    = new PilotoRequest("Carlos", 38, "1", "111.444.777-35", null, "ATPL", "MAT123", "ATIVO");
        PilotoResponse salvo      = pilotoService.salvarPiloto(original);

        PilotoRequest atualizado  = new PilotoRequest("Carlos Silva", 38, "1", "111.444.777-35", null, "ATPL", "MAT123", "ATIVO");
        PilotoResponse editado    = pilotoService.atualizarPiloto(salvo.id(), atualizado);

        assertNotNull(editado);
        assertEquals("Carlos Silva", editado.nome());
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar piloto inexistente")
    void deveRetornarErroAoAtualizarPilotoInexistente() {
        PilotoRequest request = new PilotoRequest("Carlos Silva", 38, "1", "111.444.777-35", null, "ATPL", "MAT123", "ATIVO");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.atualizarPiloto(666L, request));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }
}