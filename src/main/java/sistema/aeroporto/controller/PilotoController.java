package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sistema.aeroporto.model.Piloto;
import sistema.aeroporto.service.PilotoService;

@RestController
@RequestMapping("/pilotos")
public class PilotoController {

    @Autowired
    private PilotoService pilotoService;

    // Listar todos os pilotos
    @GetMapping
    public ResponseEntity<List<Piloto>> listarTodos() {
        return ResponseEntity.ok(pilotoService.listarTodosPilotos());
    }

    // Buscar piloto por CPF
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Piloto> buscarPorCpf(@PathVariable String cpf) {
        Piloto piloto = pilotoService.buscarPorCpf(cpf);
        return ResponseEntity.ok(piloto);
    }

    // Buscar piloto por matrícula
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Piloto> buscarPorMatricula(@PathVariable String matricula) {
        Piloto piloto = pilotoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(piloto);
    }

    // Criar piloto
    @PostMapping
    public ResponseEntity<Piloto> criarPiloto(@RequestBody Piloto piloto) {
        Piloto novoPiloto = pilotoService.salvarPiloto(piloto);
        return ResponseEntity.ok(novoPiloto);
    }

    // Deletar piloto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPiloto(@PathVariable Long id) {
        pilotoService.deletarPiloto(id);
        return ResponseEntity.noContent().build();
    }
}
