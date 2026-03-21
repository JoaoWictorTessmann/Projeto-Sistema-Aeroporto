package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.aeroporto.dto.request.PilotoUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import sistema.aeroporto.dto.request.PilotoRequest;
import sistema.aeroporto.dto.response.PilotoResponse;
import sistema.aeroporto.service.PilotoService;

@RestController
@RequestMapping("/api/pilotos")
@Tag(name = "Pilotos", description = "Gerenciamento de pilotos — cadastro, consulta, atualização e remoção")
public class PilotoController {

        @Autowired
        private PilotoService pilotoService;

        @Operation(summary = "Cadastrar novo piloto", description = "Cria um novo piloto no sistema. O CPF é validado e deve ser único. A matrícula é gerada automaticamente no formato PIL{ano}{id}.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "1")))
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Piloto cadastrado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "CPF inválido ou já cadastrado, ou nome não informado")
        })
        @PostMapping
        public ResponseEntity<PilotoResponse> criarPiloto(@RequestBody @Valid PilotoRequest request) {
                return ResponseEntity.status(201).body(pilotoService.salvarPiloto(request));
        }

        @Operation(summary = "Atualizar dados do piloto", description = "Atualiza nome, idade, gênero e status de um piloto existente. CPF e matrícula não podem ser alterados por esta rota.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "2")))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piloto atualizado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Piloto não encontrado")
        })
        @PutMapping("/{id}")
        public ResponseEntity<PilotoResponse> atualizarPiloto(
                        @Parameter(description = "ID do piloto") @PathVariable Long id,
                        @RequestBody @Valid PilotoUpdateRequest request) {
                return ResponseEntity.ok(pilotoService.atualizarPiloto(id, request));
        }

        @Operation(summary = "Remover piloto", description = "Remove um piloto do sistema pelo ID. Atenção: a operação é irreversível.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "3")))
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Piloto removido com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Piloto não encontrado")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletarPiloto(
                        @Parameter(description = "ID do piloto a ser removido") @PathVariable Long id) {
                pilotoService.deletarPiloto(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Listar todos os pilotos", description = "Retorna a lista completa de pilotos cadastrados no sistema.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "4")))
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
        @GetMapping
        public ResponseEntity<List<PilotoResponse>> listarTodos() {
                return ResponseEntity.ok(pilotoService.listarTodosPilotos());
        }

        @Operation(summary = "Buscar piloto por ID", description = "Retorna os dados de um piloto específico pelo seu identificador.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "5")))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piloto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Piloto não encontrado")
        })
        @GetMapping("/{id}")
        public ResponseEntity<PilotoResponse> buscarPorId(
                        @Parameter(description = "ID do piloto") @PathVariable Long id) {
                return ResponseEntity.ok(pilotoService.buscarPorId(id));
        }

        @Operation(summary = "Buscar piloto por CPF", description = "Busca um piloto pelo CPF. Envie apenas os números, sem pontos ou traço.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "6")))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piloto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Piloto não encontrado")
        })
        @GetMapping("/cpf/{cpf}")
        public ResponseEntity<PilotoResponse> buscarPorCpf(
                        @Parameter(description = "CPF do piloto — somente números. Ex: 11144477735") @PathVariable String cpf) {
                return ResponseEntity.ok(pilotoService.buscarPorCpf(cpf));
        }

        @Operation(summary = "Buscar piloto por matrícula", description = "Busca um piloto pela matrícula gerada automaticamente no cadastro. Formato: PIL{ano}{id}.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "7")))
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Piloto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Piloto não encontrado")
        })
        @GetMapping("/matricula/{matricula}")
        public ResponseEntity<PilotoResponse> buscarPorMatricula(
                        @Parameter(description = "Matrícula do piloto. Ex: PIL20260001") @PathVariable String matricula) {
                return ResponseEntity.ok(pilotoService.buscarPorMatricula(matricula));
        }
}