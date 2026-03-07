package sistema.aeroporto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sistema.aeroporto.dto.CompanhiaAereaDTO;
import sistema.aeroporto.model.CompanhiaAerea;
import sistema.aeroporto.service.CompanhiaAereaService;

@RestController
@RequestMapping("/companhias")
public class CompanhiaAereaController {

    @Autowired
    private CompanhiaAereaService companhiaService;

    // Listar todas as companhias
    @GetMapping
    public ResponseEntity<List<CompanhiaAerea>> listarTodas() {
        return ResponseEntity.ok(companhiaService.listarTodasCompanhias());
    }

    //buscar companhia por ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanhiaAerea> buscarPorId(@PathVariable Long id) {
        CompanhiaAerea companhia = companhiaService.buscarPorId(id);
        return ResponseEntity.ok(companhia);
    }

    // Buscar companhia por nome
    @GetMapping("/nome/{nome}")
    public ResponseEntity<CompanhiaAerea> buscarPorNome(@PathVariable String nome) {
        CompanhiaAerea companhia = companhiaService.buscarPorNome(nome);
        return ResponseEntity.ok(companhia);
    }

    // Buscar companhia por CNPJ
    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<CompanhiaAerea> buscarPorCnpj(@PathVariable String cnpj) {
        CompanhiaAerea companhia = companhiaService.buscarPorCnpj(cnpj);
        return ResponseEntity.ok(companhia);
    }

    // Criar companhia
    @PostMapping
    public ResponseEntity<CompanhiaAerea> criarCompanhia(@RequestBody CompanhiaAereaDTO companhia) {
        CompanhiaAerea novaCompanhia = companhiaService.salvarCompanhia(companhia);
        return ResponseEntity.ok(novaCompanhia);
    }

    // Deletar companhia
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompanhia(@PathVariable Long id) {
        companhiaService.deletarCompanhia(id);
        return ResponseEntity.noContent().build();
    }

    // Atualizar companhia
    @PutMapping("/{id}")
    public ResponseEntity<CompanhiaAerea> atualizarCompanhia(@PathVariable Long id, @RequestBody CompanhiaAereaDTO companhiaAtualizada) {
        CompanhiaAerea companhia = companhiaService.atualizarCompanhia(id, companhiaAtualizada);
        return ResponseEntity.ok(companhia);
    }
}
