package com.mndro.calista.data.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * The {@code RegistrationDTO} class represents a data transfer object (DTO) used for user registration.
 * @author : Muhammad Fauzan
 */

@Getter
@Setter
public class AuthRequestDTO {
    private String username;
    private String password;

    /**
     * Default constructor for the {@code RegistrationDTO} class.
     */
    public AuthRequestDTO() {
        super();
    }

    /**
     * Parameterized constructor for the {@code RegistrationDTO} class.
     *
     * @param username The username provided during registration.
     * @param password The password provided during registration.
     */
    public AuthRequestDTO(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * Set the username provided during registration.
     *
     * @param username The username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set the password provided during registration.
     *
     * @param password The password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get a string representation of the registration information.
     *
     * @return A string containing registration info including the username and password.
     */
    @Override
    public String toString() {
        return "Registration info: username: " + this.username + " password: " + this.password;
    }
}
