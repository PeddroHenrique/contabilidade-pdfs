package br.com.testprojeto.testprojeto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    @GetMapping
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String registered,
                        Model model) {

        if (error != null) {
            model.addAttribute("error", "Nome do usuário ou senha inválidos!");
        } else if (registered != null) {
            model.addAttribute("message", "Registro realizado com sucesso! Por favor, faça login.");
        } else if (logout != null) {
            model.addAttribute("message", "Você foi desconectado com sucesso!");
        }

        return "login";
    }
}
