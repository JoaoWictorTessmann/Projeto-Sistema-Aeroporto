package sistema.aeroporto.exception;

public class CpfInvalidoException extends RuntimeException {

    public CpfInvalidoException() {
        super("CPF Inválido");
    }
}
