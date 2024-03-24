package ru.prodcontest.controllers;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prodcontest.models.UserModel;
import ru.prodcontest.service.TokenService;
import ru.prodcontest.service.UserService;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, Object> request) {
        if (request == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("reason", "Request body is missing");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        System.out.println(request);
        String login = (String) request.get("login");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String countryCode = (String) request.get("countryCode");
        Boolean isPublic = (Boolean) request.get("isPublic");
        String phone = (String) request.get("phone");
        String image = (String) request.get("image");

        int resultCode = userService.createUser(login, email, password, countryCode, isPublic, phone, image);
        if (resultCode == 0) {
            System.out.println("User registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(createSuccessResponse(login, email, password, countryCode, isPublic, phone, image));
        } else {
            return handleErrorResponse(resultCode);
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Object> singInUser(@RequestBody Map<String, Object> request){
        if(request == null){
            Map<String, Object> response = new HashMap<>();
            response.put("reason", "Request body is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String login = (String) request.get("login");
        String password = (String) request.get("password");
        UserModel user = userService.getUserByLogin(login);
        if(user == null){
            Map<String, Object> response = new HashMap<>();
            response.put("reason", "Invalid login or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String storedPassword = userService.getStoredPassword(login);


        if(!passwordEncoder.matches(password, storedPassword)){
            Map<String, Object> response = new HashMap<>();
            response.put("reason", "Invalid login or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(user.getId().toString());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createSuccessResponse(String login, String email, String password, String countryCode, Boolean isPublic, String phone, String image) {
        Map<String, Object> response = new HashMap<>();
        UserModel user = new UserModel();
        user.setEmail(email);
        user.setPhone(phone);
        user.setIsPublic(isPublic);
        user.setImage(image);
        user.setPassword(password);
        user.setLogin(login);
        user.setCountryCode(countryCode);
        user.setLastPasswordUpdate(LocalDateTime.now());
        response.put("profile", user);
        return response;
    }

    private ResponseEntity<Object> handleErrorResponse(int resultCode) {
        String reason;
        HttpStatus status;
        switch (resultCode) {
            case 2:
                reason = "Missing required fields";
                status = HttpStatus.BAD_REQUEST;
                break;
            case 3:
                reason = "User with provided credentials already exists";
                status = HttpStatus.CONFLICT;
                break;
            case 5:
                reason = "Invalid password format";
                status = HttpStatus.BAD_REQUEST;
                break;
            case 6:
                reason = "Invalid login format or lengths";
                status = HttpStatus.BAD_REQUEST;
                break;
            case 7:
                reason = "Invalid email format";
                status = HttpStatus.BAD_REQUEST;
                break;
            case 8:
                reason = "Invalid countryCode format";
                status = HttpStatus.BAD_REQUEST;
                break;
            case 9:
                reason = "Invalid phone number format";
                status = HttpStatus.BAD_REQUEST;
                break;
            case 10:
                reason = "Image URL is too long";
                status = HttpStatus.BAD_REQUEST;
                break;
            default:
                reason = "Unknown error occurred";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("reason", reason);
        return ResponseEntity.status(status).body(response);
    }
}
