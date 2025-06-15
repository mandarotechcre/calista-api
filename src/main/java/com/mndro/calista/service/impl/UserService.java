package com.mndro.calista.service.impl;

import com.mndro.calista.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * This service class is responsible for loading user details by username
 * and is used for authentication and authorization purposes.
 *
 * @author [Your Name]
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads user details by username for authentication and authorization.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object representing the user.
     * @throws UsernameNotFoundException If the user with the specified username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not valid"));
    }
}
