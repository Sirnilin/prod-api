package ru.prodcontest.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.prodcontest.models.UserModel;
import ru.prodcontest.repositories.UserRepositories;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserRepositories userRepositories;
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
    private static final String SECRET_KEY = "iHL06IcJomlEur7mEzBAx4SggMqzq8CO68kLSJj8gm60H2jYNpL2LpDARo+DEqSHWqff1s/FbDRO3LVhR6dkbw==";

    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            String userId = claims.getSubject();
            Optional<UserModel> optionalUser = userRepositories.findById(Long.parseLong(userId));

            if (optionalUser.isPresent()) {
                UserModel user = optionalUser.get();
                LocalDateTime lastPasswordUpdate = user.getLastPasswordUpdate(); // предположим, что у вас есть поле с датой последнего обновления пароля
                Date tokenIssuedAt = claims.getIssuedAt();

                // Преобразование Date в LocalDateTime
                Instant instant = tokenIssuedAt.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDateTime tokenIssuedAtLocalDateTime = instant.atZone(zoneId).toLocalDateTime();

                // Если пароль был обновлен после выдачи токена, считаем токен недействительным
                if (lastPasswordUpdate.isAfter(tokenIssuedAtLocalDateTime)) {
                    return false;
                }

                // Иначе токен считается действительным
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<UserModel> getUserByToken(String token){
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
            String userId = claims.getSubject();
            return userRepositories.findById(Long.parseLong(userId));
        }catch (Exception e){
            throw new RuntimeException("Ошибка при получении пользователя по токену", e);
        }
    }
}
