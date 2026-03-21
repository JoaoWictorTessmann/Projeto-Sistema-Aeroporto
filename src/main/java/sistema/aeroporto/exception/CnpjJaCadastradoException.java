package sistema.aeroporto.exception;

public class CnpjJaCadastradoException extends RuntimeException {

    public CnpjJaCadastradoException() {
        super("CNPJ já cadastrado");
    }
}
