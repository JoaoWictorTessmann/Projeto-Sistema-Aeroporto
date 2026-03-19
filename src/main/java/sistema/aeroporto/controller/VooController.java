package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import sistema.aeroporto.dto.request.VooRequest;
import sistema.aeroporto.dto.request.VooUpdateRequest;
import sistema.aeroporto.dto.response.VooResponse;
import sistema.aeroporto.service.VooService;

@RestController
@RequestMapping("/voos")
@Tag(name = "Voos", description = "Gerenciamento de voos — criação, consulta, atualização, início e cancelamento")
public class VooController {

    @Autowired
    private VooService vooService;

    @Operation(summary = "Criar novo voo", description = "Cadastra um novo voo no sistema. Requer piloto ativo, companhia ativa, código único e horário de partida no futuro.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "1")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos — ex: origem igual ao destino, horário no passado, código duplicado"),
            @ApiResponse(responseCode = "404", description = "Piloto ou companhia não encontrados")
    })
    @PostMapping
    public ResponseEntity<VooResponse> criarVoo(@RequestBody @Valid VooRequest request) {
        return ResponseEntity.status(201).body(vooService.criarVoo(request));
    }

    @Operation(summary = "Atualizar dados do voo", description = "Atualiza horário de partida real, horário de chegada real e/ou status do voo. Usado normalmente para concluir ou registrar atrasos.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "2")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voo atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Voo não encontrado")
    })
    @PutMapping("/{vooId}")
    public ResponseEntity<VooResponse> atualizarVoo(
            @Parameter(description = "ID do voo") @PathVariable Long vooId,
            @RequestBody @Valid VooUpdateRequest request) {
        return ResponseEntity.ok(vooService.atualizarVoo(vooId, request));
    }

    @Operation(summary = "Iniciar voo", description = "Marca o voo como EM_VOO e registra o horário de partida real. O voo precisa estar com status AGENDADO e o piloto deve estar ativo.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "3")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voo iniciado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Voo não está agendado ou piloto está inativo"),
            @ApiResponse(responseCode = "404", description = "Voo não encontrado")
    })
    @PostMapping("/iniciar/{vooId}")
    public ResponseEntity<VooResponse> iniciarVoo(
            @Parameter(description = "ID do voo a ser iniciado") @PathVariable Long vooId) {
        return ResponseEntity.ok(vooService.iniciarVoo(vooId));
    }

    @Operation(summary = "Finalizar voo", description = "Finaliza um voo que está em andamento", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "4")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voo finalizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Voo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Voo não está em andamento")
    })

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<VooResponse> finalizarVoo(@PathVariable Long id) {
        return ResponseEntity.ok(vooService.finalizarVoo(id));
    }

    @Operation(summary = "Cancelar voo", description = "Cancela um voo informando obrigatoriamente o motivo. Ex: ?motivoCancelamento=Mau tempo", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "5")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voo cancelado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Motivo de cancelamento não informado"),
            @ApiResponse(responseCode = "404", description = "Voo não encontrado")
    })
    @PatchMapping("/cancelar/{vooId}")
    public ResponseEntity<VooResponse> cancelarVoo(
            @Parameter(description = "ID do voo a ser cancelado") @PathVariable Long vooId,
            @Parameter(description = "Motivo do cancelamento — obrigatório") @RequestParam String motivoCancelamento) {
        return ResponseEntity.ok(vooService.cancelarVoo(vooId, motivoCancelamento));
    }

    @Operation(summary = "Listar todos os voos", description = "Retorna a lista completa de voos cadastrados, com dados de piloto e companhia embutidos.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "6")))
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<VooResponse>> listarTodos() {
        return ResponseEntity.ok(vooService.listarTodos());
    }

    @Operation(summary = "Buscar voo por ID", description = "Retorna os detalhes completos de um voo específico pelo seu identificador.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "7")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voo encontrado"),
            @ApiResponse(responseCode = "404", description = "Voo não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VooResponse> buscarPorId(
            @Parameter(description = "ID do voo") @PathVariable Long id) {
        return ResponseEntity.ok(vooService.buscarPorId(id));
    }

    @Operation(summary = "Buscar voos por status", description = "Filtra voos pelo status atual. Valores aceitos: AGENDADO, EM_VOO, CONCLUIDO, CANCELADO.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "8")))
    @ApiResponse(responseCode = "200", description = "Lista de voos com o status informado")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<VooResponse>> buscarPorStatus(
            @Parameter(description = "Status do voo. Ex: AGENDADO, EM_VOO, CONCLUIDO, CANCELADO") @PathVariable String status) {
        return ResponseEntity.ok(vooService.buscarPorStatus(status));
    }

    @Operation(summary = "Buscar voos por piloto", description = "Retorna todos os voos associados a um piloto específico.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "9")))
    @ApiResponse(responseCode = "200", description = "Lista de voos do piloto")
    @GetMapping("/piloto/{pilotoId}")
    public ResponseEntity<List<VooResponse>> buscarPorPiloto(
            @Parameter(description = "ID do piloto") @PathVariable Long pilotoId) {
        return ResponseEntity.ok(vooService.buscarPorPiloto(pilotoId));
    }

    @Operation(summary = "Buscar voos por companhia", description = "Retorna todos os voos de uma companhia aérea específica.", extensions = @Extension(properties = @ExtensionProperty(name = "x-order", value = "10")))
    @ApiResponse(responseCode = "200", description = "Lista de voos da companhia")
    @GetMapping("/companhia/{companhiaId}")
    public ResponseEntity<List<VooResponse>> buscarPorCompanhia(
            @Parameter(description = "ID da companhia aérea") @PathVariable Long companhiaId) {
        return ResponseEntity.ok(vooService.buscarPorCompanhia(companhiaId));
    }
}