package sistema.aeroporto.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sistema.aeroporto.service.CompanhiaAereaService;
import sistema.aeroporto.service.PilotoService;
import sistema.aeroporto.service.VooService;

@Controller
@RequestMapping("/voos")
public class VooViewController {

    @Autowired
    private VooService vooService;

    @Autowired
    private PilotoService pilotoService;

    @Autowired
    private CompanhiaAereaService companhiaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("voos", vooService.listarTodos());
        return "voo/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("voo", vooService.buscarPorId(id));
        return "voo/detalhe";
    }

    @GetMapping("/novo")
    public String formularioNovo(Model model) {
        model.addAttribute("pilotos", pilotoService.listarTodosPilotos());
        model.addAttribute("companhias", companhiaService.listarTodasCompanhias());
        return "voo/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("voo", vooService.buscarPorId(id));
        model.addAttribute("pilotos", pilotoService.listarTodosPilotos());
        model.addAttribute("companhias", companhiaService.listarTodasCompanhias());
        return "voo/formulario";
    }

    @GetMapping("/status/{status}")
    public String listarPorStatus(@PathVariable String status, Model model) {
        model.addAttribute("voos", vooService.buscarPorStatus(status));
        model.addAttribute("statusFiltro", status);
        return "voo/lista";
    }
}