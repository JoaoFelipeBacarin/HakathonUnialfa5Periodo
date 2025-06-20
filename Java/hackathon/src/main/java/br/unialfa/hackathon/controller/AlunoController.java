package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.model.Aluno;
import br.unialfa.hackathon.service.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("aluno")
public class AlunoController {

    @Autowired
    private AlunoService service;

    public String iniciar(Aluno aluno, Model model) {
        return "Aluno/formulario";
    }
}
