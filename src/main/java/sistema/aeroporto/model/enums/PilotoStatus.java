package sistema.aeroporto.model.enums;

public enum PilotoStatus {
    ATIVO,
    INATIVO;

    public static PilotoStatus fromString(String status) {
        return PilotoStatus.valueOf(status.toUpperCase());
    }
}
