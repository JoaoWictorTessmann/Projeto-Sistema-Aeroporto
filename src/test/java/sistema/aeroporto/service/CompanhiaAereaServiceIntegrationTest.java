package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;

@SpringBootTest
@Transactional
public class CompanhiaAereaServiceIntegrationTest {

    @Autowired
    private CompanhiaAereaService companhiaAereaService;

    @Test
    @DisplayName("Deve listar todas as companhias do banco")
    void deveListarTodasCompanhias() {
        // arrange
        CompanhiaAerea companhiaAereaAzul = new CompanhiaAerea();
        companhiaAereaAzul.setNome("Azul");
        companhiaAereaAzul.setCnpj("05.451.308/0001-77");

        CompanhiaAerea companhiaAereaGol = new CompanhiaAerea();
        companhiaAereaGol.setNome("Gol");
        companhiaAereaGol.setCnpj("72.132.761/0001-08");

        companhiaAereaService.salvarCompanhia(companhiaAereaAzul);
        companhiaAereaService.salvarCompanhia(companhiaAereaGol);

        // act
        List<CompanhiaAerea> listaDeCompanhias = companhiaAereaService.listarTodasCompanhias();

        // assert
        assertEquals(2, listaDeCompanhias.size());
        assertEquals("Azul", listaDeCompanhias.get(0).getNome());
        assertEquals("Gol", listaDeCompanhias.get(1).getNome());

    }

    @Test
    @DisplayName("Deve buscar companhia por nome")
    void deveBuscarCompanhiaPorNome() {
        // Arange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        companhiaAereaService.salvarCompanhia(companhiaAerea);

        // act
        CompanhiaAerea companhiaEncontrada = companhiaAereaService.buscarPorNome("Azul");

        // assert
        assertEquals("Azul", companhiaEncontrada.getNome());
    }

    @Test
    @DisplayName("Deve falhar ao buscar companhia por nome inexistente")
    void deveFalharAoBuscarCompanhiaPorNomeInexistente() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        companhiaAereaService.salvarCompanhia(companhiaAerea);

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.buscarPorNome("Gol"));

        // assert
        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar companhia por CNPJ")
    void deveBuscarCompanhiaPorCnpj() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        companhiaAereaService.salvarCompanhia(companhiaAerea);

        // act
        CompanhiaAerea companhiaEncontrada = companhiaAereaService.buscarPorCnpj("05.451.308/0001-77");
        
        // assert
        assertEquals("05.451.308/0001-77", companhiaEncontrada.getCnpj());
    }

    @Test
    @DisplayName("Deve falhar ao buscar companhia por CNPJ inexistente")
    void deveFalharAoBuscarCompanhiaPorCnpjInexistente() {

        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        companhiaAereaService.salvarCompanhia(companhiaAerea);

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.buscarPorCnpj("05.451.308/0002-77"));

        // assert
        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar companhia com CNPJ válido")
    void deveSalvarCompanhiaComCnpjValido() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");

        // act
        CompanhiaAerea companhiaSalva = companhiaAereaService.salvarCompanhia(companhiaAerea);

        // assert
        assertEquals("05.451.308/0001-77", companhiaSalva.getCnpj());
    }

    @Test
    @DisplayName("Deve falhar ao salvar companhia com CNPJ inválido")
    void deveFalharAoSalvarCompanhiaComCnpjInvalido() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("123");

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.salvarCompanhia(companhiaAerea));

        // assert
        assertEquals("CNPJ inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao salvar companhia com CNPJ já existente")
    void deveFalharAoSalvarCompanhiaComCnpjJaExistente() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        companhiaAereaService.salvarCompanhia(companhiaAerea);

        CompanhiaAerea companhiaAerea2 = new CompanhiaAerea();
        companhiaAerea2.setNome("Gol");
        companhiaAerea2.setCnpj("05.451.308/0001-77");

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.salvarCompanhia(companhiaAerea2));

        // assert
        assertEquals("CNPJ já cadastrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar companhia")
    void deveDeletarCompanhia() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        CompanhiaAerea companhiaSalva = companhiaAereaService.salvarCompanhia(companhiaAerea);

        // act
        companhiaAereaService.deletarCompanhia(companhiaSalva.getId());

        // assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.buscarPorId(companhiaSalva.getId()));

        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao deletar companhia inexistente")
    void deveFalharAoDeletarCompanhiaInexistente() {
        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.deletarCompanhia(666L));

        // assert
        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar companhia existente")
    void deveAtualizarCompanhiaExistente() {
        // arrange
        CompanhiaAerea companhiaAerea = new CompanhiaAerea();
        companhiaAerea.setNome("Azul");
        companhiaAerea.setCnpj("05.451.308/0001-77");
        CompanhiaAerea companhiaSalva = companhiaAereaService.salvarCompanhia(companhiaAerea);

        CompanhiaAerea companhiaAtualizada = new CompanhiaAerea();
        companhiaAtualizada.setStatus(CompanhiaAereaStatus.INATIVA);

        // act
        CompanhiaAerea result = companhiaAereaService.atualizarCompanhia(companhiaSalva.getId(), companhiaAtualizada);

        // assert
            assertEquals(companhiaSalva.getId(), result.getId());
            assertEquals("Azul", result.getNome());
            assertEquals("05.451.308/0001-77", result.getCnpj());
            assertEquals(CompanhiaAereaStatus.INATIVA, result.getStatus());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar companhia inexistente")
    void deveFalharAoAtualizarCompanhiaInexistente() {
        // arrange
        CompanhiaAerea companhiaAtualizada = new CompanhiaAerea();
        companhiaAtualizada.setStatus(CompanhiaAereaStatus.INATIVA);

        // act
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.atualizarCompanhia(666L, companhiaAtualizada));

        // assert
        assertEquals("Companhia não encontrada", exception.getMessage());
    }
}