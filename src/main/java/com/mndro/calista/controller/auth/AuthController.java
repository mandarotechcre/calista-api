package com.mndro.calista.controller.auth;

import com.mndro.calista.data.dto.CommonResponseDTO;
import com.mndro.calista.data.dto.request.AuthRequestDTO;
import com.mndro.calista.data.dto.response.AuthResponseDto;
import com.mndro.calista.entity.User;
import com.mndro.calista.service.impl.AuthenticationService;
import com.mndro.calista.util.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponseDTO<User>> registerUser(@RequestBody AuthRequestDTO body){
        User user = authenticationService.registerUser(body.getUsername(), body.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDTO.<User>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Success register new user")
                        .data(user).build()
                );
    }

    /**
     * Logs in a user.
     *
     * @param body The login credentials.
     * @return A response containing login information.
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDTO<AuthResponseDto>> loginUser(@RequestBody AuthRequestDTO body){
        AuthResponseDto authResponseDto = authenticationService.loginUser(body.getUsername(), body.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDTO.<AuthResponseDto>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Success login")
                        .data(authResponseDto).build()
                );
    }
}
