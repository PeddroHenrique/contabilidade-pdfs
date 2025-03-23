package br.com.testprojeto.testprojeto.controller;

import br.com.testprojeto.testprojeto.dto.UserDto;
import br.com.testprojeto.testprojeto.model.User;
import br.com.testprojeto.testprojeto.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/registro")
public class RegistroController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String registrarUsuario(Model model) {
        model.addAttribute("user", new UserDto());
        return "registro";
    }

    @PostMapping
    public String salvarUsuario(@ModelAttribute("user") @Valid UserDto userDto,
                                BindingResult result,
                                Model model) {

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "As senhas não conferem");
        }

        if (result.hasErrors()) {
            return "registro";
        }

        try {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(userDto.getPassword());

            userService.registerUser(user);

            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registro";
        }
    }
}
