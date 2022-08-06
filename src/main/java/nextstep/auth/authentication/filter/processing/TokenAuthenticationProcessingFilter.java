package nextstep.auth.authentication.filter.processing;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.auth.authentication.AuthenticationException;
import nextstep.auth.authentication.AuthenticationToken;
import nextstep.auth.authentication.UserDetailsService;
import nextstep.auth.context.Authentication;
import nextstep.auth.token.JwtTokenProvider;
import nextstep.auth.token.TokenRequest;
import nextstep.auth.token.TokenResponse;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class TokenAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenAuthenticationProcessingFilter(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected AuthenticationToken convert(HttpServletRequest request) throws IOException {
        var content = request
                .getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));

        var tokenRequest = new ObjectMapper().readValue(content, TokenRequest.class);

        return new AuthenticationToken(tokenRequest.getEmail(), tokenRequest.getPassword());
    }

    @Override
    protected Authentication authenticate(AuthenticationToken authenticationToken) {

        var userDetails = userDetailsService.loadUserByUsername(authenticationToken.getPrincipal());

        if (userDetails == null) {
            throw new AuthenticationException();
        }

        if (!userDetails.getPassword().equals(authenticationToken.getCredentials())) {
            throw new AuthenticationException();
        }

        return new Authentication(userDetails.getUsername(), userDetails.getAuthorities());
    }

    @Override
    protected void processing(Authentication authentication, HttpServletResponse response) throws Exception {
        var token = jwtTokenProvider.createToken((String) authentication.getPrincipal(), authentication.getAuthorities());
        TokenResponse tokenResponse = new TokenResponse(token);

        String responseToClient = new ObjectMapper().writeValueAsString(tokenResponse);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().print(responseToClient);
    }
}