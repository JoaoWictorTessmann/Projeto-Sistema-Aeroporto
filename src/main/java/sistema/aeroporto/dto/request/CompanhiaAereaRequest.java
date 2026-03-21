package sistema.aeroporto.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanhiaAereaRequest(

        @NotBlank(message = "Nome é obrigatório") String nome,

        @NotBlank(message = "CNPJ é obrigatório") String cnpj,

        LocalDate dataFundacao,
        Boolean seguroAeronave,

        @NotNull(message = "Status é obrigatório") String status) {
}