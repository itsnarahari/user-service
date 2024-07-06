package com.user.security;

import com.user.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenServiceProvider tokenServiceProvider;

    @Autowired
    @Lazy
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        setAuth(request);
        filterChain.doFilter(request, response);
        stopWatch.stop();
        logger.info("URI: {} Method: {} executionTime: {}", request.getRequestURI(),
                request.getMethod(), stopWatch.getTotalTime(TimeUnit.SECONDS));

    }

    public void setAuth(HttpServletRequest request){
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {

                String userName = tokenServiceProvider.extractUserName(jwt);

                if (StringUtils.hasText(userName)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = authService.loadUserByUsername(userName);
                    if (tokenServiceProvider.isTokenValid(jwt, userDetails)) {
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {

        if(!ObjectUtils.isEmpty(request.getCookies()) && request.getCookies().length>0){
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equalsIgnoreCase("token") && !ObjectUtils.isEmpty(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }else{
            String bearerToken = request.getHeader("Authorization");
            logger.info("bearerToken {}", bearerToken);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }
        return null;
    }

}
