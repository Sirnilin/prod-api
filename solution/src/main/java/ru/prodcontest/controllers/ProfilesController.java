package ru.prodcontest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.prodcontest.models.UserModel;
import ru.prodcontest.service.TokenService;
import ru.prodcontest.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfilesController {
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/{login}")
    public ResponseEntity<Object> getProfileUserByLogin(@RequestHeader("Authorization") String token, @PathVariable String login){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    UserModel tempUser = userService.getUserProfileByLogin(login, user.get());
                    if(tempUser != null){
                        return ResponseEntity.ok(tempUser);
                    }else{
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "The user with the specified username does not exist, or the sender of the request does not have access to the requested profile");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("reason", "Error when receiving the user profile");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("reason", "Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
