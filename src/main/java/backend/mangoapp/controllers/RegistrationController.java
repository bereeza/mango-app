package backend.mangoapp.controllers;

import backend.mangoapp.entity.User;
import backend.mangoapp.service.userService.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class RegistrationController {

    @Autowired
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> signUp(@RequestBody User user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.add(user));
    }

    @GetMapping("/signin")
    public ResponseEntity<String> signIn() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("signin");
    }
}
