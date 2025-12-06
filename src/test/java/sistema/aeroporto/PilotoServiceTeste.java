package sistema.aeroporto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.repository.PilotoRepository;
import sistema.aeroporto.service.PilotoService;
import sistema.aeroporto.model.enums.PilotoStatus;

public class PilotoServiceTeste {

    @Mock
    private PilotoRepository pilotoRepository;

    @InjectMocks
    private PilotoService pilotoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------------------------------------------------
    // LISTAR TODOS
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Listar todos os pilotos
     * OBJETIVO: Verificar se o serviço retorna todos os pilotos cadastrados.
     * CONDIÇÕES: Mock retornando dois pilotos.
     * RESULTADO ESPERADO: Lista com tamanho 2.
     * ============================
     */
    @Test
    void deveListarTodosPilotos() {
        when(pilotoRepository.findAll()).thenReturn(Arrays.asList(new Piloto(), new Piloto()));
        assertEquals(2, pilotoService.listarTodosPilotos().size());
    }

    // ----------------------------------------------------------
    // BUSCAR POR CPF
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Buscar piloto por CPF existente
     * OBJETIVO: Retornar piloto quando CPF está cadastrado.
     * CONDIÇÕES: Mock retornando Optional contendo piloto.
     * RESULTADO ESPERADO: Retorno não-nulo.
     * ============================
     */
    @Test
    void deveBuscarPorCpfExistente() {
        Piloto p = new Piloto();
        p.setCpf("123");

        when(pilotoRepository.findByCpf("123")).thenReturn(Optional.of(p));

        assertNotNull(pilotoService.buscarPorCpf("123"));
    }

    /*
     * ============================
     * TESTE: Buscar piloto por CPF inexistente
     * OBJETIVO: Retornar null quando CPF não for encontrado.
     * CONDIÇÕES: Mock retornando Optional.empty().
     * RESULTADO ESPERADO: Retorno null.
     * ============================
     */
    @Test
    void deveRetornarNullQuandoCpfNaoExiste() {
        when(pilotoRepository.findByCpf("999")).thenReturn(Optional.empty());
        assertNull(pilotoService.buscarPorCpf("999"));
    }

    // ----------------------------------------------------------
    // SALVAR
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Salvar piloto e gerar matrícula
     * OBJETIVO: Garantir que o piloto salvo receba uma matrícula válida.
     * CONDIÇÕES: Mock do save() retornando piloto com ID.
     * RESULTADO ESPERADO: Matrícula iniciando com "PIL".
     * ============================
     */
    @Test
    void deveSalvarPilotoGerandoMatricula() {
        Piloto p = new Piloto();
        p.setNome("Teste");
        p.setCpf("11144477735");

        Piloto salvo = new Piloto();
        salvo.setId(10L);

        when(pilotoRepository.save(any())).thenReturn(salvo);

        Piloto resultado = pilotoService.salvarPiloto(p);

        assertTrue(resultado.getMatricula().startsWith("PIL"));
    }

    // ----------------------------------------------------------
    // DELETAR
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Deletar piloto existente
     * OBJETIVO: Verificar se o método de exclusão funciona corretamente.
     * CONDIÇÕES: deleteById configurado com comportamento vazio.
     * RESULTADO ESPERADO: Método deleteById executado uma vez.
     * ============================
     */
    @Test
    void deveDeletarPiloto() {
        doNothing().when(pilotoRepository).deleteById(1L);
        pilotoService.deletarPiloto(1L);
        verify(pilotoRepository, times(1)).deleteById(1L);
    }

    // ----------------------------------------------------------
    // ATUALIZAR
    // ----------------------------------------------------------

    /*
     * ============================
     * TESTE: Atualizar piloto existente
     * OBJETIVO: Atualizar dados importantes do piloto.
     * CONDIÇÕES: findById retorna piloto existente e save devolve o mesmo.
     * RESULTADO ESPERADO: Status e data de renovação atualizados.
     * ============================
     */
    @Test
    void deveAtualizarPiloto() {
        Piloto existente = new Piloto();
        existente.setId(1L);

        Piloto atualizado = new Piloto();
        atualizado.setStatus(PilotoStatus.ATIVO);
        atualizado.setDataRenovacao(LocalDate.now());

        when(pilotoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pilotoRepository.save(existente)).thenReturn(existente);

        Piloto result = pilotoService.atualizarPiloto(1L, atualizado);

        assertEquals(atualizado.getStatus(), result.getStatus());
        assertEquals(atualizado.getDataRenovacao(), result.getDataRenovacao());
    }

    /*
     * ============================
     * TESTE: Atualizar piloto inexistente
     * OBJETIVO: Evitar atualização quando ID não existe.
     * CONDIÇÕES: findById retornando Optional.empty().
     * RESULTADO ESPERADO: Retorno null.
     * ============================
     */
    @Test
    void deveRetornarNullAoAtualizarPilotoInexistente() {
        when(pilotoRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(pilotoService.atualizarPiloto(2L, new Piloto()));
    }
}
