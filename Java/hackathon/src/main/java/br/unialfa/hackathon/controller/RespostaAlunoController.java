//package br.unialfa.hackathon.controller;
//
//import br.unialfa.hackathon.service.ProvaService;
//import br.unialfa.hackathon.service.RespostaAlunoService;
//import br.unialfa.hackathon.service.UsuarioService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@Controller
//@RequestMapping("/respostas")
//@RequiredArgsConstructor
//public class RespostaAlunoController {
//
//    private final RespostaAlunoService respostaService;
//    private final UsuarioService usuarioService;
//    private final ProvaService provaService;
//
//    @PostMapping("/enviar")
//    public String enviarResposta(
//            @RequestParam Long alunoId,
//            @RequestParam Long provaId,
//            @RequestParam String respostas
//    ) {
//        var aluno = usuarioService.buscarPorId(alunoId).orElseThrow();
//        var prova = provaService.listarTodas().stream().filter(p -> p.getId().equals(provaId)).findFirst().orElseThrow();
//        respostaService.corrigirERegistrar(prova, aluno, respostas);
//        return "redirect:/aluno/notas";
//    }
//}
