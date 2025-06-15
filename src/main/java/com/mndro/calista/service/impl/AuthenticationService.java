package com.mndro.calista.service.impl;

import com.mndro.calista.data.dto.response.AuthResponseDto;
import com.mndro.calista.entity.Role;
import com.mndro.calista.entity.User;
import com.mndro.calista.repository.RoleRepository;
import com.mndro.calista.repository.UserRepository;
import com.mndro.calista.util.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public User registerUser(String username, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").orElse(null);

        if (userRole == null) {
            // Handle the case when the USER role is not found.
            throw new RuntimeException("USER role not found.");
        }

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        return userRepository.save(new User(UUID.randomUUID(), username, encodedPassword, authorities));
    }

    public AuthResponseDto loginUser(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = tokenService.generateJwt(auth);
            return new AuthResponseDto(userRepository.findByUsername(username).orElse(null), token);
        } catch (AuthenticationException e) {
            return new AuthResponseDto(null, "");
        }
    }
}
