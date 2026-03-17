package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import sistema.aeroporto.dto.request.VooRequest;
import sistema.aeroporto.dto.response.VooResponse;
import sistema.aeroporto.service.VooService;

@RestController
@RequestMapping("/voos")
public class VooController {

    @Autowired
    private VooService vooService;

    @PostMapping
    public ResponseEntity<VooResponse> criarVoo(@RequestBody @Valid VooRequest request) {
        return ResponseEntity.status(201).body(vooService.criarVoo(request));
    }

    @GetMapping
    public ResponseEntity<List<VooResponse>> listarTodos() {
        return ResponseEntity.ok(vooService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VooResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vooService.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<VooResponse>> buscarPorStatus(@PathVariable String status) {
        // ← PathVariable String, não mais VooDTO
        return ResponseEntity.ok(vooService.buscarPorStatus(status));
    }

    @GetMapping("/piloto/{pilotoId}")
    public ResponseEntity<List<VooResponse>> buscarPorPiloto(@PathVariable Long pilotoId) {
        return ResponseEntity.ok(vooService.buscarPorPiloto(pilotoId));
    }

    @GetMapping("/companhia/{companhiaId}")
    public ResponseEntity<List<VooResponse>> buscarPorCompanhia(@PathVariable Long companhiaId) {
        return ResponseEntity.ok(vooService.buscarPorCompanhia(companhiaId));
    }

    @PostMapping("/iniciar/{vooId}")
    public ResponseEntity<VooResponse> iniciarVoo(@PathVariable Long vooId) {
        return ResponseEntity.ok(vooService.iniciarVoo(vooId));
    }

    @PatchMapping("/cancelar/{vooId}")
    // ← trocado de @DeleteMapping para @PATCH (cancelar é atualização, não deleção)
    public ResponseEntity<VooResponse> cancelarVoo(
            @PathVariable Long vooId,
            @RequestParam String motivoCancelamento) {
        // ← @RequestParam String direto, não mais VooDTO
        return ResponseEntity.ok(vooService.cancelarVoo(vooId, motivoCancelamento));
    }

    @PutMapping("/{vooId}")
    public ResponseEntity<VooResponse> atualizarVoo(
            @PathVariable Long vooId,
            @RequestBody @Valid VooRequest request) {
        return ResponseEntity.ok(vooService.atualizarVoo(vooId, request));
    }
}