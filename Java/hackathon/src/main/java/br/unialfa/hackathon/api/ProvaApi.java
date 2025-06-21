//// src/main/java/br/unialfa/hackathon/api/ProvaApi.java
//package br.unialfa.hackathon.api; // Certifique-se de que o pacote está correto
//
//import br.unialfa.hackathon.dto.ProvaResponse; // <--- Importar o DTO
//import br.unialfa.hackathon.service.ProvaService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/turmas/{turmaId}/provas")
//public class ProvaApi {
//
//    private final ProvaService provaService;
//
//    public ProvaApi(ProvaService provaService) {
//        this.provaService = provaService;
//    }
//
//    @GetMapping
//    public List<ProvaResponse> listarProvasPorTurma(@PathVariable Long turmaId) { // <--- Altere o tipo de retorno para List<ProvaResponse>
//        return provaService.buscarProvasPorTurmaId(turmaId); // <--- Chame o novo método
//    }
//}