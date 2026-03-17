package sistema.aeroporto.model.enums;

public enum VooStatus {
    AGENDADO,
    EM_VOO,
    CANCELADO,
    CONCLUIDO;

     public static VooStatus fromString(String status) {
        return VooStatus.valueOf(status.toUpperCase());
    }
}
