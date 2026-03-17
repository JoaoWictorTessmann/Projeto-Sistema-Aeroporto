package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import sistema.aeroporto.dto.request.CompanhiaAereaRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;

@SpringBootTest
@Transactional
public class CompanhiaAereaServiceIntegrationTest {

    @Autowired
    private CompanhiaAereaService companhiaAereaService;

    @Test
    @DisplayName("Deve listar todas as companhias do banco")
    void deveListarTodasCompanhias() {
        CompanhiaAereaRequest azul = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        CompanhiaAereaRequest gol  = new CompanhiaAereaRequest("Gol",  "81.797.711/0001-30", null, true, "ATIVA");

        companhiaAereaService.salvarCompanhia(azul);
        companhiaAereaService.salvarCompanhia(gol);

        List<CompanhiaAereaResponse> lista = companhiaAereaService.listarTodasCompanhias();

        assertEquals(2, lista.size());
        assertEquals("Azul", lista.get(0).nome());
        assertEquals("Gol",  lista.get(1).nome());
    }

    @Test
    @DisplayName("Deve buscar companhia por nome")
    void deveBuscarCompanhiaPorNome() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        companhiaAereaService.salvarCompanhia(request);

        CompanhiaAereaResponse response = companhiaAereaService.buscarPorNome("Azul");

        assertEquals("Azul", response.nome());
    }

    @Test
    @DisplayName("Deve falhar ao buscar companhia por nome inexistente")
    void deveFalharAoBuscarCompanhiaPorNomeInexistente() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        companhiaAereaService.salvarCompanhia(request);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.buscarPorNome("Gol"));

        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar companhia por CNPJ")
    void deveBuscarCompanhiaPorCnpj() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        companhiaAereaService.salvarCompanhia(request);

        CompanhiaAereaResponse response = companhiaAereaService.buscarPorCnpj("63.141.461/0001-02");

        assertEquals("63.141.461/0001-02", response.cnpj());
    }

    @Test
    @DisplayName("Deve falhar ao buscar companhia por CNPJ inexistente")
    void deveFalharAoBuscarCompanhiaPorCnpjInexistente() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        companhiaAereaService.salvarCompanhia(request);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.buscarPorCnpj("05.451.308/0002-77"));

        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar companhia com CNPJ válido")
    void deveSalvarCompanhiaComCnpjValido() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "55.044.476/0001-16", null, true, "ATIVA");

        CompanhiaAereaResponse response = companhiaAereaService.salvarCompanhia(request);

        assertEquals("55.044.476/0001-16", response.cnpj());
    }

    @Test
    @DisplayName("Deve falhar ao salvar companhia com CNPJ inválido")
    void deveFalharAoSalvarCompanhiaComCnpjInvalido() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-03", null, true, "ATIVA");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.salvarCompanhia(request));

        assertEquals("CNPJ inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao salvar companhia com CNPJ já existente")
    void deveFalharAoSalvarCompanhiaComCnpjJaExistente() {
        CompanhiaAereaRequest azul = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        companhiaAereaService.salvarCompanhia(azul);

        CompanhiaAereaRequest gol = new CompanhiaAereaRequest("Gol", "63.141.461/0001-02", null, true, "ATIVA");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.salvarCompanhia(gol));

        assertEquals("CNPJ já cadastrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar companhia")
    void deveDeletarCompanhia() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        CompanhiaAereaResponse salva = companhiaAereaService.salvarCompanhia(request);

        companhiaAereaService.deletarCompanhia(salva.id());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.buscarPorId(salva.id()));

        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao deletar companhia inexistente")
    void deveFalharAoDeletarCompanhiaInexistente() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.deletarCompanhia(666L));

        assertEquals("Companhia não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar companhia existente")
    void deveAtualizarCompanhiaExistente() {
        CompanhiaAereaRequest original = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "ATIVA");
        CompanhiaAereaResponse salva = companhiaAereaService.salvarCompanhia(original);

        CompanhiaAereaRequest atualizacao = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "INATIVA");
        CompanhiaAereaResponse result = companhiaAereaService.atualizarCompanhia(salva.id(), atualizacao);

        assertEquals(salva.id(), result.id());
        assertEquals("Azul", result.nome());
        assertEquals("63.141.461/0001-02", result.cnpj());
        assertEquals(CompanhiaAereaStatus.INATIVA.name(), result.status());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar companhia inexistente")
    void deveFalharAoAtualizarCompanhiaInexistente() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "63.141.461/0001-02", null, true, "INATIVA");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> companhiaAereaService.atualizarCompanhia(666L, request));

        assertEquals("Companhia não encontrada", exception.getMessage());
    }
}