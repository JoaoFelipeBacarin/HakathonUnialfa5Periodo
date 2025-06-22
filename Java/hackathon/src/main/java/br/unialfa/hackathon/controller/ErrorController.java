package br.unialfa.hackathon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error/403")
    public String acessoNegado() {
        return "error/403"; // nome do template dentro de /templates/error/
    }
}
