package br.unialfa.hackathon.controller;

import br.unialfa.hackathon.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("turma")
public class TurmaController {

    @Autowired
    private TurmaService service;
}
