package sistema.aeroporto.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice(basePackages = "sistema.aeroporto.controller")
public class GlobalExceptionHandler {

        // 404 - Não encontrados
        @ExceptionHandler({
                        NotFoundCompanhiaAereaException.class,
                        NotFoundPilotoException.class,
                        NotFoundVooException.class
        })
        public ResponseEntity<?> handleNotFound(RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                                "status", 404,
                                "message", ex.getMessage()));
        }

        // 409 - Conflitos / regras de negócio
        @ExceptionHandler({
                        CnpjInvalidoException.class,
                        CnpjJaCadastradoException.class,
                        CodigoVooExistenteException.class,
                        CodigoVooObrigatorioException.class,
                        CompanhiaNaoAtivaException.class,
                        CpfInvalidoException.class,
                        CpfJaCadastradoException.class,
                        CpfObrigatorioException.class,
                        HorarioPartidaObrigatorioException.class,
                        HorarioPartidaPassadoException.class,
                        MenorIdadeException.class,
                        MotivoCancelamentoObrigatorioException.class,
                        NomeJaCadastradoException.class,
                        NomeObrigatorioException.class,
                        OrigemDestinoIguaisException.class,
                        OrigemDestinoObrigatorioException.class,
                        PilotoInativoException.class,
                        PilotoObrigatorioException.class,
                        PilotoOutroVooException.class,
                        SemPilotoException.class,
                        SomenteAgendadoException.class,
                        SomenteEmVooException.class
        })
        public ResponseEntity<?> handleBusinessRule(RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                                "status", 409,
                                "message", ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
                String mensagem = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(e -> e.getDefaultMessage())
                                .findFirst()
                                .orElse("Dados inválidos");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                                "status", 400,
                                "message", mensagem));
        }

        // 500 - Qualquer coisa não tratada
        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleGeneric(Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                                "status", 500,
                                "message", "Erro interno no servidor"));
        }
}