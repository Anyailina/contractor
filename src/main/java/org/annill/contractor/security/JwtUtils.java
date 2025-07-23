package org.annill.contractor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Утилитный класс для работы с JWT токенами. Генерация, валидация и извлечение данных из токенов.
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${spring.jwt.jwtSecret}")
    private String jwtSecret;

    /**
     * Получает ключ для подписи токена.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Извлекает имя пользователя из токена.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Извлекает роли пользователя из токена.
     */
    public List<String> getRoles(String token) {
        Claims claims = Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        return claims.get("roles", List.class);
    }

    /**
     * Валидирует JWT токен.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Невалидный JWT: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT срок действия окончен: {}", e.getMessage());
        }

        return false;
    }

}
