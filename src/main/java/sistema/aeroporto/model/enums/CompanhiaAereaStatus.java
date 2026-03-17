package sistema.aeroporto.model.enums;

public enum CompanhiaAereaStatus {
    ATIVA,
    INATIVA;

     public static CompanhiaAereaStatus fromString(String status) {
        return CompanhiaAereaStatus.valueOf(status.toUpperCase());
    }
}