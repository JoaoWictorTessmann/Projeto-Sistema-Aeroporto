package sistema.aeroporto.dto;

public record CompanhiaAereaDTO(
    String id,
    String nome,
    String cnpj,
    Boolean seguroAeronave,
    String status
){}
