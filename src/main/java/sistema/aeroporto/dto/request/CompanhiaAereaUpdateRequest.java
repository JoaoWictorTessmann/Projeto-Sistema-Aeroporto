package sistema.aeroporto.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanhiaAereaUpdateRequest(

    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotNull(message = "Seguro de aeronave é obrigatório")
    Boolean seguroAeronave,

    @NotBlank(message = "Status é obrigatório")
    String status
) {}