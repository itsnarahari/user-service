package com.user.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.AppProperties;
import com.user.dto.TokenDetails;
import com.user.dto.UserDto;
import com.user.entities.User;
import com.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class TokenServiceProvider {



    private static final Logger logger = LoggerFactory.getLogger(TokenServiceProvider.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getId);
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    public TokenDetails generateToken(Map<String, Object> extraClaims) {
        Date issuedDate = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() +appProperties.getAuth().getTokenExpirationMsec());
        String token = Jwts.builder().setClaims(extraClaims).setSubject("")
                .setIssuedAt(issuedDate)
                .setId((String) extraClaims.get("username"))
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setToken(token);
        tokenDetails.setAlgo(SignatureAlgorithm.HS256.getDescription());
        tokenDetails.setSub(appProperties.getAuth().getSub());
        tokenDetails.setIat(issuedDate);
        tokenDetails.setExp(expirationDate);
        return tokenDetails;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    public TokenDetails getTokenDetails(String token){
        Claims claims = extractAllClaims(token);
        TokenDetails tokenDetails = new TokenDetails();
        tokenDetails.setToken(token);
        tokenDetails.setSub(claims.getSubject());
        tokenDetails.setIat(claims.getIssuedAt());
        tokenDetails.setExp(claims.getExpiration());
        tokenDetails.setIssuer(claims.getIssuer());
        tokenDetails.setId(claims.getId());

        UserDto userDto = new UserDto();
        userDto.setUsername(String.valueOf(claims.get("username")));
        userDto.setUserId(((Number) claims.get("userId")).longValue());
        userDto.setImageUrl(String.valueOf(claims.get("imageUrl")));
        userDto.setVerified((Boolean) claims.get("verified"));

        tokenDetails.setUserDto(userDto);
        return tokenDetails;
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Optional<User> byUsername = userRepository.findByUsername(userPrincipal.getUsername());
        logger.info("userPrincipal.getAuthorities() {}", userPrincipal.getAttributes());
        User user = byUsername.orElse(null);
        Map<String, Object> map = new HashMap<>();
        assert user != null;
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("imageUrl", user.getImageUrl());
        map.put("verified",user.getVerified());
        return generateToken(map).getToken();
    }
}
