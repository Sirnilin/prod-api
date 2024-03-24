package ru.prodcontest.controllers;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.prodcontest.models.UserModel;
import ru.prodcontest.repositories.UserRepositories;
import ru.prodcontest.service.TokenService;
import ru.prodcontest.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@RequestHeader("Authorization") String token){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if (user.isPresent()) {
                    return ResponseEntity.ok(user.get());
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

    @PatchMapping("/profile")
    public ResponseEntity<Object> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    if(request != null){
                        String countryCode = (String) request.get("countryCode");
                        Boolean isPublic = (Boolean) request.get("isPublic");
                        String phone = (String) request.get("phone");
                        String image = (String) request.get("image");

                        int resultCode = userService.updateUserInfo(countryCode, isPublic, phone, image, user.get());
                        System.out.println(resultCode);
                        if(resultCode == 0){
                            UserModel tempUser = userService.getUserByLogin(user.get().getLogin());
                            return ResponseEntity.ok(tempUser);
                        }else {
                            Map<String, Object> response = new HashMap<>();
                            if(resultCode == 1){
                                response.put("reason", "Invalid country code");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                            }
                            if(resultCode == 2){
                                response.put("reason", "Invalid phone");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                            }
                            if(resultCode == 3){
                                response.put("reason", "Invalid image");
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                            }
                            if(resultCode == 4){
                                response.put("reason", "User with provided credentials already exists");
                                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                            }
                        }

                    }else {
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "Invalid date body");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @PostMapping("/updatePassword")
    public ResponseEntity<Object> updatePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        String oldPassword = (String) request.get("oldPassword");
                        String newPassword = (String) request.get("newPassword");

                        int resultCode = userService.updatePassword(oldPassword, newPassword, user.get());
                        if(resultCode == 0){
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "ok");
                            return ResponseEntity.status(HttpStatus.OK).body(result);
                        }
                        if(resultCode == 1){
                            Map<String, Object> response = new HashMap<>();
                            response.put("reason", "The specified password does not match the valid one");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                        if(resultCode == 2){
                            Map<String, Object> response = new HashMap<>();
                            response.put("reason", "The new password does not meet the security requirements");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }
                    }else {
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "Error when receiving the user profile");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                    }
                }else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("reason", "Invalid date body");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("reason", "Invalid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
