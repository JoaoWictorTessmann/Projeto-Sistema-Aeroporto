package sistema.aeroporto.exception;

public class CpfObrigatorioException extends RuntimeException {

    public CpfObrigatorioException() {
        super("CPF Obrigatório");
    }
}