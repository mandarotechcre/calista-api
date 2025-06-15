package com.mndro.calista.data.dto.response;

import com.mndro.calista.entity.User;
import lombok.Getter;


/**
 * The {@code LoginResponseDTO} class represents a response containing user information and a JSON Web Token (JWT)
 * after a successful login.
 */
@Getter
public class AuthResponseDto {
    private User user;
    private String jwt;

    /**
     * Default constructor for the {@code LoginResponseDTO} class.
     */
    public AuthResponseDto() {
        super();
    }

    /**
     * Parameterized constructor for the {@code LoginResponseDTO} class.
     *
     * @param user The user information associated with the login.
     * @param jwt  The JSON Web Token (JWT) generated after a successful login.
     */
    public AuthResponseDto(User user, String jwt) {
        this.user = user;
        this.jwt = jwt;
    }

    /**
     * Set the user information associated with the login.
     *
     * @param user The user information.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Set the JSON Web Token (JWT) generated after a successful login.
     *
     * @param jwt The JSON Web Token (JWT).
     */
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}