package sistema.aeroporto.dto;

public record PilotoDTO(
    String id,
    String nome,
    String idade,
    String genero,
    String cpf,
    String habilitacao,
    String matricula,
    String status
){}