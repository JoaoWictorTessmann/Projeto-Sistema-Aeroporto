package sistema.aeroporto.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record VooUpdateRequest(

    LocalDateTime horarioPartidaReal,
    LocalDateTime horarioChegadaReal,

    @NotBlank(message = "Status é obrigatório")
    String status
) {}