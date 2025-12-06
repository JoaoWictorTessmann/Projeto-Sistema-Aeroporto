package sistema.aeroporto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
import sistema.aeroporto.service.CompanhiaAereaService;
import sistema.aeroporto.util.CnpjUtils;

public class CompanhiaAereaServiceTest {

    @InjectMocks
    private CompanhiaAereaService companhiaService;

    @Mock
    private CompanhiaAereaRepository companhiaRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------
    // LISTAR TODAS
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Listar todas as companhias
     * OBJETIVO: Verificar se o serviço retorna todas as companhias cadastradas.
     * CONDIÇÕES: Mock retornando uma lista com duas companhias.
     * RESULTADO ESPERADO: Lista contendo exatamente 2 elementos.
     * ============================
     */
    @Test
    void deveListarTodasCompanhias() {
        when(companhiaRepository.findAll()).thenReturn(List.of(new CompanhiaAerea(), new CompanhiaAerea()));

        List<CompanhiaAerea> lista = companhiaService.listarTodasCompanhias();

        assertEquals(2, lista.size());
    }

    // ----------------------------------------------------------
    // BUSCAR POR NOME
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Buscar companhia por nome existente
     * OBJETIVO: Verificar retorno correto ao buscar por nome existente.
     * CONDIÇÕES: Mock devolvendo Optional com a companhia "Azul".
     * RESULTADO ESPERADO: Retornar objeto com nome "Azul".
     * ============================
     */
    @Test
    void deveBuscarCompanhiaPorNome() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setNome("Azul");

        when(companhiaRepository.findByNome("Azul")).thenReturn(Optional.of(c));

        CompanhiaAerea result = companhiaService.buscarPorNome("Azul");

        assertEquals("Azul", result.getNome());
    }

    /*
     * ============================
     * TESTE: Buscar companhia por nome inexistente
     * OBJETIVO: Garantir que o método lança exceção quando não encontra.
     * CONDIÇÕES: Mock retornando Optional.empty().
     * RESULTADO ESPERADO: Lançar RuntimeException com mensagem adequada.
     * ============================
     */
    @Test
    void deveFalharAoBuscarCompanhiaPorNomeInexistente() {
        when(companhiaRepository.findByNome("X")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            companhiaService.buscarPorNome("X");
        });

        assertEquals("Companhia não encontrada", ex.getMessage());
    }

    // ----------------------------------------------------------
    // BUSCAR POR CNPJ
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Buscar companhia por CNPJ existente
     * OBJETIVO: Garantir retorno correto ao buscar por CNPJ válido.
     * CONDIÇÕES: Mock retornando companhia com o CNPJ informado.
     * RESULTADO ESPERADO: Retornar objeto com CNPJ correto.
     * ============================
     */
    @Test
    void deveBuscarCompanhiaPorCnpj() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setCnpj("12345678000199");

        when(companhiaRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(c));

        CompanhiaAerea result = companhiaService.buscarPorCnpj("12345678000199");

        assertEquals("12345678000199", result.getCnpj());
    }

    /*
     * ============================
     * TESTE: Buscar companhia por CNPJ inexistente
     * OBJETIVO: Garantir exceção quando CNPJ não existe.
     * CONDIÇÕES: Mock retornando Optional.empty().
     * RESULTADO ESPERADO: Lançar RuntimeException "Companhia não encontrada".
     * ============================
     */
    @Test
    void deveFalharAoBuscarCompanhiaPorCnpjInexistente() {
        when(companhiaRepository.findByCnpj("000")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            companhiaService.buscarPorCnpj("000");
        });

        assertEquals("Companhia não encontrada", ex.getMessage());
    }

    // ----------------------------------------------------------
    // SALVAR
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Salvar companhia com CNPJ válido
     * OBJETIVO: Garantir que companhias com CNPJ válido são salvas.
     * CONDIÇÕES: CnpjUtils.validarCnpj retornando true; CNPJ não duplicado.
     * RESULTADO ESPERADO: Objeto salvo corretamente.
     * ============================
     */
    @Test
    void deveSalvarCompanhiaComCnpjValido() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setCnpj("40510225000102");

        try (MockedStatic<CnpjUtils> mock = mockStatic(CnpjUtils.class)) {
            mock.when(() -> CnpjUtils.validarCnpj("40510225000102")).thenReturn(true);

            when(companhiaRepository.existsByCnpj("40510225000102")).thenReturn(false);
            when(companhiaRepository.save(c)).thenReturn(c);

            CompanhiaAerea result = companhiaService.salvarCompanhia(c);

            assertEquals("40510225000102", result.getCnpj());
        }
    }

    /*
     * ============================
     * TESTE: Salvar com CNPJ inválido
     * OBJETIVO: Verificar erro quando CNPJ é inválido.
     * CONDIÇÕES: CnpjUtils.validarCnpj retornando false.
     * RESULTADO ESPERADO: Lançar RuntimeException "CNPJ inválido".
     * ============================
     */
    @Test
    void deveFalharAoSalvarCompanhiaComCnpjInvalido() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setCnpj("000");

        try (MockedStatic<CnpjUtils> mock = mockStatic(CnpjUtils.class)) {
            mock.when(() -> CnpjUtils.validarCnpj("000")).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                companhiaService.salvarCompanhia(c);
            });

            assertEquals("CNPJ inválido", ex.getMessage());
        }
    }

    /*
     * ============================
     * TESTE: Salvar com CNPJ duplicado
     * OBJETIVO: Garantir erro quando CNPJ já existe no sistema.
     * CONDIÇÕES: existsByCnpj retornando true.
     * RESULTADO ESPERADO: Lançar RuntimeException "CNPJ já cadastrado".
     * ============================
     */
    @Test
    void deveFalharAoSalvarCompanhiaComCnpjDuplicado() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setCnpj("40510225000102");

        try (MockedStatic<CnpjUtils> mock = mockStatic(CnpjUtils.class)) {
            mock.when(() -> CnpjUtils.validarCnpj("40510225000102")).thenReturn(true);
            when(companhiaRepository.existsByCnpj("40510225000102")).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                companhiaService.salvarCompanhia(c);
            });

            assertEquals("CNPJ já cadastrado", ex.getMessage());
        }
    }

    // ----------------------------------------------------------
    // DELETAR
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Deletar companhia existente
     * OBJETIVO: Verificar remoção correta quando ID existe.
     * CONDIÇÕES: existsById retornando true.
     * RESULTADO ESPERADO: deleteById chamado uma vez, sem exceções.
     * ============================
     */
    @Test
    void deveDeletarCompanhia() {
        when(companhiaRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> companhiaService.deletarCompanhia(1L));

        verify(companhiaRepository, times(1)).deleteById(1L);
    }

    /*
     * ============================
     * TESTE: Falha ao deletar companhia inexistente
     * OBJETIVO: Garantir lançamento de erro.
     * CONDIÇÕES: existsById retornando false.
     * RESULTADO ESPERADO: RuntimeException "Companhia não encontrada".
     * ============================
     */
    @Test
    void deveFalharAoDeletarCompanhiaInexistente() {
        when(companhiaRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            companhiaService.deletarCompanhia(1L);
        });

        assertEquals("Companhia não encontrada", ex.getMessage());
    }

    // ----------------------------------------------------------
    // ATUALIZAR
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Atualizar companhia existente
     * OBJETIVO: Verificar alteração de status funcionando corretamente.
     * CONDIÇÕES: findById retornando companhia existente.
     * RESULTADO ESPERADO: Status alterado para o novo valor.
     * ============================
     */
    @Test
    void deveAtualizarCompanhia() {
        CompanhiaAerea existente = new CompanhiaAerea();
        existente.setId(1L);
        existente.setStatus(CompanhiaAereaStatus.ATIVA);

        CompanhiaAerea atualizada = new CompanhiaAerea();
        atualizada.setStatus(CompanhiaAereaStatus.INATIVA);

        when(companhiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(companhiaRepository.save(existente)).thenReturn(existente);

        CompanhiaAerea result = companhiaService.atualizarCompanhia(1L, atualizada);

        assertEquals(CompanhiaAereaStatus.INATIVA, result.getStatus());
    }

    /*
     * ============================
     * TESTE: Atualizar companhia inexistente
     * OBJETIVO: Garantir erro quando ID não existe.
     * CONDIÇÕES: findById retornando empty.
     * RESULTADO ESPERADO: RuntimeException "Companhia não encontrada".
     * ============================
     */
    @Test
    void deveFalharAoAtualizarCompanhiaInexistente() {
        when(companhiaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            companhiaService.atualizarCompanhia(1L, new CompanhiaAerea());
        });

        assertEquals("Companhia não encontrada", ex.getMessage());
    }
}
