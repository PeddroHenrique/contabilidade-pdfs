package br.com.testprojeto.testprojeto.service;

import br.com.testprojeto.testprojeto.model.User;
import br.com.testprojeto.testprojeto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username já existe!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }
}
