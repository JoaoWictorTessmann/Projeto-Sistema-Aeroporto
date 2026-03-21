package sistema.aeroporto.exception;

public class SomenteAgendadoException extends RuntimeException {

    public SomenteAgendadoException() {
        super("Somente voos agendados podem ser iniciados");
    }
}
