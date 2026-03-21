package sistema.aeroporto.dto.response;

import java.time.LocalDate;

public record PilotoResponse(
        Long id,
        String nome,
        Integer idade,
        String genero,
        String cpf,
        LocalDate dataRenovacao,
        String matricula,
        String habilitacao,
        String status) {
}