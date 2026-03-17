package sistema.aeroporto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import sistema.aeroporto.dto.request.CompanhiaAereaRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import sistema.aeroporto.repository.CompanhiaAereaRepository;
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

    @Test
    void deveListarTodasCompanhias() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setId(1L);
        c.setNome("Azul");
        c.setCnpj("12345");
        c.setStatus(CompanhiaAereaStatus.ATIVA);

        when(companhiaRepository.findAll()).thenReturn(List.of(c, c));

        List<CompanhiaAereaResponse> lista = companhiaService.listarTodasCompanhias();

        assertEquals(2, lista.size());
    }

    @Test
    void deveBuscarCompanhiaPorNome() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setId(1L);
        c.setNome("Azul");
        c.setStatus(CompanhiaAereaStatus.ATIVA);

        when(companhiaRepository.findByNome("Azul")).thenReturn(Optional.of(c));

        CompanhiaAereaResponse result = companhiaService.buscarPorNome("Azul");

        assertEquals("Azul", result.nome());
    }

    @Test
    void deveFalharAoBuscarCompanhiaPorNomeInexistente() {
        when(companhiaRepository.findByNome("X")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> companhiaService.buscarPorNome("X"));

        assertEquals("Companhia não encontrada", ex.getMessage());
    }

    @Test
    void deveBuscarCompanhiaPorCnpj() {
        CompanhiaAerea c = new CompanhiaAerea();
        c.setId(1L);
        c.setCnpj("12345678000199");
        c.setStatus(CompanhiaAereaStatus.ATIVA);

        when(companhiaRepository.findByCnpj("12345678000199")).thenReturn(Optional.of(c));

        CompanhiaAereaResponse result = companhiaService.buscarPorCnpj("12345678000199");

        assertEquals("12345678000199", result.cnpj());
    }

    @Test
    void deveFalharAoBuscarCompanhiaPorCnpjInexistente() {
        when(companhiaRepository.findByCnpj("000")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> companhiaService.buscarPorCnpj("000"));

        assertEquals("Companhia não encontrada", ex.getMessage());
    }

    @Test
    void deveSalvarCompanhiaComCnpjValido() {
        String cnpj = "28.818.940/0001-01";
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", cnpj, null, true, "ATIVA");

        CompanhiaAerea entidade = new CompanhiaAerea();
        entidade.setId(1L);
        entidade.setNome("Azul");
        entidade.setCnpj(cnpj);
        entidade.setSeguroAeronave(true);
        entidade.setStatus(CompanhiaAereaStatus.ATIVA);

        try (MockedStatic<CnpjUtils> mock = mockStatic(CnpjUtils.class)) {
            mock.when(() -> CnpjUtils.validarCnpj(cnpj)).thenReturn(true);
            when(companhiaRepository.existsByCnpj(cnpj)).thenReturn(false);
            when(companhiaRepository.existsByNome("Azul")).thenReturn(false);
            when(companhiaRepository.save(any(CompanhiaAerea.class))).thenReturn(entidade);

            CompanhiaAereaResponse result = companhiaService.salvarCompanhia(request);

            assertEquals(cnpj, result.cnpj());
        }
    }

    @Test
    void deveFalharAoSalvarCompanhiaComCnpjInvalido() {
        String cnpj = "28.818.940/0001-01";
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", cnpj, null, true, "ATIVA");

        try (MockedStatic<CnpjUtils> mock = mockStatic(CnpjUtils.class)) {
            mock.when(() -> CnpjUtils.validarCnpj(cnpj)).thenReturn(false);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> companhiaService.salvarCompanhia(request));

            assertEquals("CNPJ inválido", ex.getMessage());
        }
    }

    @Test
    void deveFalharAoSalvarCompanhiaComCnpjDuplicado() {
        String cnpj = "28.818.940/0001-01";
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", cnpj, null, true, "ATIVA");

        try (MockedStatic<CnpjUtils> mock = mockStatic(CnpjUtils.class)) {
            mock.when(() -> CnpjUtils.validarCnpj(cnpj)).thenReturn(true);
            when(companhiaRepository.existsByCnpj(cnpj)).thenReturn(true);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> companhiaService.salvarCompanhia(request));

            assertEquals("CNPJ já cadastrado", ex.getMessage());
        }
    }

    @Test
    void deveDeletarCompanhia() {
        when(companhiaRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> companhiaService.deletarCompanhia(1L));

        verify(companhiaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveFalharAoDeletarCompanhiaInexistente() {
        when(companhiaRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> companhiaService.deletarCompanhia(1L));

        assertEquals("Companhia não encontrada", ex.getMessage());
    }

    @Test
    void deveAtualizarCompanhia() {
        CompanhiaAerea existente = new CompanhiaAerea();
        existente.setId(1L);
        existente.setNome("Azul");
        existente.setCnpj("40510225000102");
        existente.setSeguroAeronave(true);
        existente.setStatus(CompanhiaAereaStatus.INATIVA); // save devolve já atualizado

        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "40510225000102", null, true, "INATIVA");

        when(companhiaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(companhiaRepository.save(any(CompanhiaAerea.class))).thenReturn(existente);

        CompanhiaAereaResponse result = companhiaService.atualizarCompanhia(1L, request);

        assertEquals(CompanhiaAereaStatus.INATIVA.name(), result.status());
    }

    @Test
    void deveFalharAoAtualizarCompanhiaInexistente() {
        CompanhiaAereaRequest request = new CompanhiaAereaRequest("Azul", "40510225000102", null, true, "ATIVA");

        when(companhiaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> companhiaService.atualizarCompanhia(1L, request));

        assertEquals("Companhia não encontrada", ex.getMessage());
    }
}