package sistema.aeroporto.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PilotoRequest(

        @NotBlank(message = "Nome é obrigatório") String nome,

        @NotNull(message = "Idade é obrigatória") @Min(value = 18, message = "Piloto deve ter ao menos 18 anos") @Max(value = 99, message = "Idade inválida") Integer idade,

        String genero,

        @NotBlank(message = "CPF é obrigatório") String cpf,

        LocalDate dataRenovacao,

        @NotBlank(message = "Habilitação é obrigatória") String habilitacao,

        @NotBlank(message = "Status é obrigatório") String status) {
}