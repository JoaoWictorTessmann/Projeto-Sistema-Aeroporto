package sistema.aeroporto.dto.response;

import java.time.LocalDateTime;

public record VooResponse(
        Long id,
        PilotoResponse piloto,
        CompanhiaAereaResponse companhia,
        String codigo,
        String origem,
        String destino,
        LocalDateTime horarioPartidaPrevisto,
        LocalDateTime horarioChegadaPrevisto,
        LocalDateTime horarioPartidaReal,
        LocalDateTime horarioChegadaReal,
        String motivoCancelamento,
        String status) {
}