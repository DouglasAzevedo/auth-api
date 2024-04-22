package br.com.stic.templateauth.controllers;

import br.com.stic.templateauth.config.security.TokenService;
import br.com.stic.templateauth.domain.user.AuthenticationDTO;
import br.com.stic.templateauth.domain.user.LoginResponseDTO;
import br.com.stic.templateauth.domain.user.RegisterDTO;
import br.com.stic.templateauth.domain.user.User;
import br.com.stic.templateauth.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (userRepository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        String encryptPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptPassword, data.role());

        userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

}
