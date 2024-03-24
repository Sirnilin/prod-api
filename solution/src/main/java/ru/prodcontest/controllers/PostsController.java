package ru.prodcontest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.prodcontest.models.Posts;
import ru.prodcontest.models.UserModel;
import ru.prodcontest.service.TokenService;
import ru.prodcontest.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostsController {
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<Object> createPost(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> request){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                if(request != null){
                    Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                    if(user.isPresent()){
                        String content = (String) request.get("content");
                        List<String> tags = (List<String>) request.get("tags");
                        Posts post = userService.createPost(user.get(), content, tags);
                        if(post != null){
                            return ResponseEntity.status(HttpStatus.OK).body(post);
                        }else{
                            Map<String, Object> response = new HashMap<>();
                            response.put("reason", "Bad content or tags");
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

    @GetMapping("/{postId}")
    public ResponseEntity<Object> getPostById(@RequestHeader("Authorization") String token, @PathVariable String postId){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Posts post = userService.getPostById(postId, user.get());
                    if(post != null){
                        return ResponseEntity.status(HttpStatus.OK).body(post);
                    }else{
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "The specified post was not found or there is no access to it");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
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
    @GetMapping("/feed/me")
    public ResponseEntity<Object> getAllUserPost(@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "5") int limit, @RequestParam(defaultValue = "0") int offset){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    List<Posts> posts = userService.getAllUserPosts(user.get(), limit, offset);
                    return ResponseEntity.status(HttpStatus.OK).body(posts);
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
    @GetMapping("/feed/{login}")
    public ResponseEntity<Object> getAllUserPostByLogin(@RequestHeader("Authorization") String token, @RequestParam(defaultValue = "5") int limit, @RequestParam(defaultValue = "0") int offset, @PathVariable String login){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    try {
                        List<Posts> posts = userService.getAllUserPosts(login, user.get(), limit, offset);
                        return ResponseEntity.status(HttpStatus.OK).body(posts);
                    }
                    catch (Exception e){
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "The user has not been found or there is no access to it");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
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
    @PostMapping("/{postId}/like")
    public ResponseEntity<Object> likePost(@RequestHeader("Authorization") String token, @PathVariable String postId){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Posts post = userService.likePost(user.get(), postId);
                    if(post != null){
                        return ResponseEntity.status(HttpStatus.OK).body(post);
                    }else{
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "The specified post was not found or there is no access to it");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
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
    @PostMapping("/{postId}/dislike")
    public ResponseEntity<Object> dislikePost(@RequestHeader("Authorization") String token, @PathVariable String postId){
        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7);
            if(tokenService.validateToken(jwtToken)){
                Optional<UserModel> user = tokenService.getUserByToken(jwtToken);
                if(user.isPresent()){
                    Posts post = userService.dislikePost(user.get(), postId);
                    if(post != null){
                        return ResponseEntity.status(HttpStatus.OK).body(post);
                    }else{
                        Map<String, Object> response = new HashMap<>();
                        response.put("reason", "The specified post was not found or there is no access to it");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
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
