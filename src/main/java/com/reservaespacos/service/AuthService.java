package com.reservaespacos.service;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.exception.RegraDeNegocioException;
import com.reservaespacos.model.Usuario;
import com.reservaespacos.repository.UsuarioRepository;
import com.reservaespacos.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public String login(String email, String senha) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, senha)
        );
        return jwtUtils.generateToken(authentication);
    }

    public Usuario registarPublico(Dtos.RegistoRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RegraDeNegocioException(
                "Ja existe uma conta com o email: " + request.getEmail()
            );
        }

        Usuario.Role role;
        try {
            role = Usuario.Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            role = Usuario.Role.CLIENTE;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(role);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    public void alterarSenha(String email, String senhaActual, String novaSenha) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, senhaActual)
        );
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RegraDeNegocioException("Utilizador nao encontrado."));
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }
}
