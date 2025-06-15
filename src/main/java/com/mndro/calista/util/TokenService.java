package com.mndro.calista.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;


/**
 * This service class provides methods for generating JWT tokens and extracting user data from JWT tokens.
 * It also includes logging statements to track token generation and user data retrieval.
 *
 * @author Muhammad Fauzan
 * @since 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    /**
     * Generates a JWT token based on the provided authentication details.
     *
     * @param auth The authentication object containing user details.
     * @return The generated JWT token.
     */
    public String generateJwt(Authentication auth) {
        Instant now = Instant.now();
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .claim("roles", scope)
                .build();
        String jwtToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        log.info("Generated JWT Token for user '{}'", auth.getName());
        return jwtToken;
    }

    /**
     * Retrieves user data from a JWT token.
     *
     * @param jwtToken The JWT token from which to extract user data.
     * @return A UserDataDTO containing the user's username and roles.
     */
//    public UserDataDTO getUserData(String jwtToken) {
//        try {
//            Jwt jwt = jwtDecoder.decode(jwtToken);
//            String rolesClaim = jwt.getClaim("roles");
//            String subject = jwt.getSubject();
//            log.info("Getting user data for user '{}'", subject);
//            return UserDataDTO.builder()
//                    .username(subject)
//                    .roles(Arrays.asList(rolesClaim.split(" ")))
//                    .build();
//        }catch (Exception e){
//            throw new AuthenticationException("Invalid jwt token", HttpStatus.NOT_ACCEPTABLE);
//        }
//    }
}