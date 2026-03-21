package sistema.aeroporto.exception;

public class SemPilotoException extends RuntimeException {

    public SemPilotoException() {
        super("Voo sem piloto");
    }
}
