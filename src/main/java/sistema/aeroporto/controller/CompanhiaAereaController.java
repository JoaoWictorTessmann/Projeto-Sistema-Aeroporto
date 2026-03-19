package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.aeroporto.dto.request.CompanhiaAereaUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import sistema.aeroporto.dto.request.CompanhiaAereaRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.service.CompanhiaAereaService;

@RestController
@RequestMapping("/companhias")
@Tag(name = "Companhias Aéreas", description = "Gerenciamento de companhias aéreas — cadastro, consulta, atualização e remoção")
public class CompanhiaAereaController {

    @Autowired
    private CompanhiaAereaService companhiaService;

    @Operation(summary = "Cadastrar nova companhia aérea", description = "Cria uma nova companhia no sistema. O CNPJ é validado e deve ser único. O nome também deve ser único.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "1")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Companhia cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "CNPJ inválido, CNPJ já cadastrado ou nome já existente")
    })
    @PostMapping
    public ResponseEntity<CompanhiaAereaResponse> criarCompanhia(
            @RequestBody @Valid CompanhiaAereaRequest request) {
        return ResponseEntity.status(201).body(companhiaService.salvarCompanhia(request));
    }

    @Operation(summary = "Atualizar companhia aérea", description = "Atualiza nome, CNPJ, seguro de aeronave e status de uma companhia existente. Status aceitos: ATIVA, INATIVA.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "2")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Companhia atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Companhia não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CompanhiaAereaResponse> atualizarCompanhia(
            @Parameter(description = "ID da companhia") @PathVariable Long id,
            @RequestBody @Valid CompanhiaAereaUpdateRequest request) {
        return ResponseEntity.ok(companhiaService.atualizarCompanhia(id, request));
    }

    @Operation(summary = "Remover companhia aérea", description = "Remove uma companhia do sistema pelo ID. Atenção: a operação é irreversível.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "3")))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Companhia removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Companhia não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompanhia(
            @Parameter(description = "ID da companhia a ser removida") @PathVariable Long id) {
        companhiaService.deletarCompanhia(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar todas as companhias", description = "Retorna a lista completa de companhias aéreas cadastradas.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "4")))
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<CompanhiaAereaResponse>> listarTodas() {
        return ResponseEntity.ok(companhiaService.listarTodasCompanhias());
    }

    @Operation(summary = "Buscar companhia por ID", description = "Retorna os dados de uma companhia aérea pelo seu identificador.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "5")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Companhia encontrada"),
            @ApiResponse(responseCode = "404", description = "Companhia não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CompanhiaAereaResponse> buscarPorId(
            @Parameter(description = "ID da companhia") @PathVariable Long id) {
        return ResponseEntity.ok(companhiaService.buscarPorId(id));
    }

    @Operation(summary = "Buscar companhia por nome", description = "Busca uma companhia aérea pelo nome exato cadastrado.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "6")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Companhia encontrada"),
            @ApiResponse(responseCode = "404", description = "Companhia não encontrada")
    })
    @GetMapping("/nome/{nome}")
    public ResponseEntity<CompanhiaAereaResponse> buscarPorNome(
            @Parameter(description = "Nome da companhia. Ex: Azul") @PathVariable String nome) {
        return ResponseEntity.ok(companhiaService.buscarPorNome(nome));
    }

    @Operation(summary = "Buscar companhia por CNPJ", description = "Busca uma companhia aérea pelo CNPJ. Pode ser enviado com ou sem formatação.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "7")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Companhia encontrada"),
            @ApiResponse(responseCode = "404", description = "Companhia não encontrada")
    })
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CompanhiaAereaResponse> buscarPorCnpj(
            @Parameter(description = "CNPJ da companhia. Ex: 63.141.461/0001-02") @PathVariable String cnpj) {
        return ResponseEntity.ok(companhiaService.buscarPorCnpj(cnpj));
    }
}