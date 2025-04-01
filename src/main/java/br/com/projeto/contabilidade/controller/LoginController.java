package br.com.projeto.contabilidade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Autenticação", description = "Mostra a página de login")
@Controller
@RequestMapping("/api/login")
public class LoginController {

    @Operation(summary = "Página de login",
            description = "Usuários que realizarem o login com sucesso serão redirecionados para a página de listagem dos arquivos",
            method = "GET"
    )
    @GetMapping
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String registered,
                        Model model) {

        if (error != null) {
            model.addAttribute("error", "Nome do usuário ou senha inválidos!");
        } else if (logout != null) {
            model.addAttribute("message", "Você foi desconectado com sucesso!");
        } else if (registered != null) {
            model.addAttribute("message", "Registro realizado com sucesso! Por favor, faça login.");
        }

        return "login";
    }
}
