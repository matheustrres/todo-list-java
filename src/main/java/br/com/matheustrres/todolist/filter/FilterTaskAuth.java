package br.com.matheustrres.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import br.com.matheustrres.todolist.user.IUserRepository;
import br.com.matheustrres.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private IUserRepository userRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_AUTHORIZATION_PREFIX = "Basic ";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";

    private static final int UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Catch client authentication (username, password)
        String servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (authorizationHeader != null && authorizationHeader.startsWith(BASIC_AUTHORIZATION_PREFIX)) {
                try {
                    String authEncoded = authorizationHeader.substring(BASIC_AUTHORIZATION_PREFIX.length()).trim();

                    byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
                    String authString = new String(authDecoded);

                    String[] credentials = authString.split(":");
                    String username = credentials[0];
                    String password = credentials[1];

                    // Validate user
                    UserModel user = this.userRepository.findByUsername(username);

                    if (user != null) {
                        Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                        if (result.verified) {
                            request.setAttribute("userId", user.getId());
                            filterChain.doFilter(request, response);
                            
                            return;
                        }
                    }
                } catch (Exception e) {
                    response.sendError(UNAUTHORIZED, e.getMessage());
                }
            }

            response.setStatus(UNAUTHORIZED);
            response.sendError(UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE);
        }

        filterChain.doFilter(request, response);
    }
}
