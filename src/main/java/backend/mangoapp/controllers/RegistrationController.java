package backend.mangoapp.controllers;

import backend.mangoapp.entity.User;
import backend.mangoapp.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class RegistrationController {
    private final UserService userService;
    private final HttpSession session;
    @Autowired
    public RegistrationController(UserService userService, HttpSession session) {
        this.userService = userService;
        this.session = session;
    }
    @GetMapping
    public String msg() {
        return "Please, signIn or signUp.";
    }

    @PostMapping
    public ResponseEntity<User> signUp(@RequestBody User user) {
        User registeredUser = userService.add(user);
        session.setAttribute("currentUser", registeredUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registeredUser);
    }

    @GetMapping("/signin")
    public ResponseEntity<String> signIn(@RequestParam String email, @RequestParam String password) {
        Optional<User> userOptional = userService.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("currentUser", user);
                return ResponseEntity.status(HttpStatus.OK).body("User signed in successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
