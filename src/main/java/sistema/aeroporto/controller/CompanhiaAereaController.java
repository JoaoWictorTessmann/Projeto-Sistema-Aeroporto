package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import sistema.aeroporto.dto.request.CompanhiaAereaRequest;
import sistema.aeroporto.dto.response.CompanhiaAereaResponse;
import sistema.aeroporto.service.CompanhiaAereaService;

@RestController
@RequestMapping("/companhias")
public class CompanhiaAereaController {

    @Autowired
    private CompanhiaAereaService companhiaService;

    @GetMapping
    public ResponseEntity<List<CompanhiaAereaResponse>> listarTodas() {
        return ResponseEntity.ok(companhiaService.listarTodasCompanhias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanhiaAereaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(companhiaService.buscarPorId(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<CompanhiaAereaResponse> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(companhiaService.buscarPorNome(nome));
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CompanhiaAereaResponse> buscarPorCnpj(@PathVariable String cnpj) {
        return ResponseEntity.ok(companhiaService.buscarPorCnpj(cnpj));
    }

    @PostMapping
    public ResponseEntity<CompanhiaAereaResponse> criarCompanhia(@RequestBody @Valid CompanhiaAereaRequest request) {
        return ResponseEntity.status(201).body(companhiaService.salvarCompanhia(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompanhia(@PathVariable Long id) {
        companhiaService.deletarCompanhia(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanhiaAereaResponse> atualizarCompanhia(
            @PathVariable Long id,
            @RequestBody @Valid CompanhiaAereaRequest request) {
        return ResponseEntity.ok(companhiaService.atualizarCompanhia(id, request));
    }
}