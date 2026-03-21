package sistema.aeroporto.dto.response;

import java.time.LocalDate;

public record CompanhiaAereaResponse(
        Long id,
        String nome,
        String cnpj,
        LocalDate dataFundacao,
        Boolean seguroAeronave,
        String status) {
}