package sistema.aeroporto.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VooRequest(

        @NotNull(message = "Piloto é obrigatório") Long pilotoId,

        @NotNull(message = "Companhia é obrigatória") Long companhiaId,

        @NotBlank(message = "Código é obrigatório") String codigo,

        @NotBlank(message = "Origem é obrigatória") String origem,

        @NotBlank(message = "Destino é obrigatório") String destino,

        @NotNull(message = "Horário de partida previsto é obrigatório") LocalDateTime horarioPartidaPrevisto,

        LocalDateTime horarioChegadaPrevisto) {
}