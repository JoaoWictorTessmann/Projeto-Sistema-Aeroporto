package sistema.aeroporto.exception;

public class CnpjInvalidoException extends RuntimeException {

    public CnpjInvalidoException() {
        super("CNPJ inválido");
    }
}
