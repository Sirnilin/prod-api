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
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendsController {
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Object> addFriend(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        String login = (String) request.get("login");
                        if(userService.addFriend(login, user.get())){
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "ok");
                            return ResponseEntity.status(HttpStatus.OK).body(result);
                        }else{
                            Map<String, Object> response = new HashMap<>();
                            response.put("reason", "The user with the specified username was not found");
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @PostMapping("/remove")
    public ResponseEntity<Object> removeFriend(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        String login = (String) request.get("login");
                        if(userService.removeFriend(login, user.get())){
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("status", "ok");
                            return ResponseEntity.status(HttpStatus.OK).body(result);
                        }else{
                            Map<String, Object> response = new HashMap<>();
                            response.put("reason", "The user with the specified username was not found");
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @GetMapping
    public ResponseEntity<Object> getFriends(@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "5") int limit, @RequestParam(defaultValue = "0") int offset){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    return ResponseEntity.status(HttpStatus.OK).body(userService.getUserFriendsWithPagination(user.get(), limit, offset));
                }else {
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
