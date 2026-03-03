package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.model.enums.PilotoStatus;

@SpringBootTest
@Transactional
public class PilotoServiceIntegrationTest {

    @Autowired
    private PilotoService pilotoService;

    @Test
    @DisplayName("Deve listar todos os pilotos do banco")
    void deveListarTodosPilotos() {
        // arrange
        Piloto piloto1 = new Piloto();
        piloto1.setNome("João");
        piloto1.setCpf("111.444.777-35");

        Piloto piloto2 = new Piloto();
        piloto2.setNome("Maria");
        piloto2.setCpf("222.555.888-46");

        pilotoService.salvarPiloto(piloto1);
        pilotoService.salvarPiloto(piloto2);

        // act
        List<Piloto> listaDePilotos = pilotoService.listarTodosPilotos();

        // assert
        assertEquals(2, listaDePilotos.size());
        assertTrue(listaDePilotos.contains(piloto1));
        assertTrue(listaDePilotos.contains(piloto2));
    }

    @Test
    @DisplayName("Deve buscar piloto por CPF existente")
    void deveBuscarPorCpfExistente() {
        // arrange
        Piloto piloto = new Piloto();
        piloto.setNome("João");
        piloto.setCpf("111.444.777-35");

        pilotoService.salvarPiloto(piloto);

        // act
        Piloto pilotoEncontrado = pilotoService.buscarPorCpf("11144477735");

        // assert
        assertEquals(piloto, pilotoEncontrado);
    }

    @Test
    @DisplayName("Deve retornar null ao buscar por CPF inexistente")
    void deveRetornarNullQuandoCpfNaoExiste() {
        // act
        Piloto pilotoEncontrado = pilotoService.buscarPorCpf("99999999999");

        // assert
        assertEquals(null, pilotoEncontrado);

    }

    @DisplayName("Deve salvar piloto gerando matrícula automaticamente")
    @Test
    void deveSalvarPilotoGerandoMatricula() {

        // arrange
        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("111.444.777-35");

        // act
        Piloto pilotoSalvo = pilotoService.salvarPiloto(piloto);

        // assert
        assertNotNull(pilotoSalvo);

        assertEquals("Carlos", pilotoSalvo.getNome());
        assertEquals("11144477735", pilotoSalvo.getCpf());

        assertNotNull(pilotoSalvo.getMatricula());
        assertFalse(pilotoSalvo.getMatricula().isBlank());

        // valida padrão da matrícula
        assertTrue(pilotoSalvo.getMatricula().startsWith("PIL"));
    }

    @Test
    @DisplayName("Deve deletar piloto")
    void deveDeletarPiloto() {
        // arrange
        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("111.444.777-35");
        Piloto pilotoSalvo = pilotoService.salvarPiloto(piloto);

        // act
        assertDoesNotThrow(() -> pilotoService.deletarPiloto(pilotoSalvo.getId()));

        // assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.buscarPorId(pilotoSalvo.getId()));

        assertEquals("Piloto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar piloto")
    void deveAtualizarPiloto() {

        // arrange
        Piloto piloto = new Piloto();
        piloto.setNome("Carlos");
        piloto.setCpf("111.444.777-35");

        Piloto pilotoSalvo = pilotoService.salvarPiloto(piloto);

        Piloto pilotoAtualizado = new Piloto();
        pilotoAtualizado.setNome("Carlos Silva");
        pilotoAtualizado.setStatus(PilotoStatus.ATIVO);

        // act
        Piloto pilotoEditado = pilotoService.atualizarPiloto(pilotoSalvo.getId(), pilotoAtualizado);

        // assert
        assertNotNull(pilotoEditado);

        assertEquals("Carlos Silva", pilotoEditado.getNome());
        assertEquals(PilotoStatus.ATIVO, pilotoEditado.getStatus());
    }

    @Test
    @DisplayName("Deve retornar null ao atualizar piloto inexistente")
    void deveRetornarNullAoAtualizarPilotoInexistente() {
        // arrange
        Piloto pilotoAtualizado = new Piloto();
        pilotoAtualizado.setNome("Carlos Silva");
        pilotoAtualizado.setStatus(PilotoStatus.ATIVO);

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> pilotoService.atualizarPiloto(666L, pilotoAtualizado));

        // assert
        assertEquals("Piloto não encontrado", exception.getMessage());
    }
}