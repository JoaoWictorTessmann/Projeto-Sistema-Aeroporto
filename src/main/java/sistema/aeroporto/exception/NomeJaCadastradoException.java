package sistema.aeroporto.exception;

public class NomeJaCadastradoException extends RuntimeException {
    
    public NomeJaCadastradoException() {
        super("Nome já cadastrado");
    }
}
