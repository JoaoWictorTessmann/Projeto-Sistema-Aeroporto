package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import sistema.aeroporto.dto.request.PilotoRequest;
import sistema.aeroporto.dto.response.PilotoResponse;
import sistema.aeroporto.service.PilotoService;

@RestController
@RequestMapping("/pilotos")
public class PilotoController {

    @Autowired
    private PilotoService pilotoService;

    @GetMapping
    public ResponseEntity<List<PilotoResponse>> listarTodos() {
        return ResponseEntity.ok(pilotoService.listarTodosPilotos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PilotoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pilotoService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PilotoResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(pilotoService.buscarPorCpf(cpf));
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<PilotoResponse> buscarPorMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(pilotoService.buscarPorMatricula(matricula));
    }

    @PostMapping
    public ResponseEntity<PilotoResponse> criarPiloto(@RequestBody @Valid PilotoRequest request) {
        return ResponseEntity.status(201).body(pilotoService.salvarPiloto(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPiloto(@PathVariable Long id) {
        pilotoService.deletarPiloto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PilotoResponse> atualizarPiloto(
            @PathVariable Long id,
            @RequestBody @Valid PilotoRequest request) {
        return ResponseEntity.ok(pilotoService.atualizarPiloto(id, request));
    }
}