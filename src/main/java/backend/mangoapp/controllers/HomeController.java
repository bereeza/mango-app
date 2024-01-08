package backend.mangoapp.controllers;

import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.service.postService.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final PostService postService;
    private final HttpSession session;

    @Autowired
    public HomeController(PostService postService, HttpSession session) {
        this.postService = postService;
        this.session = session;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        User currentUser = (User) session.getAttribute("currentUser");
        System.out.println(currentUser);
        return ResponseEntity.ok(postService.getAll());
    }
}
