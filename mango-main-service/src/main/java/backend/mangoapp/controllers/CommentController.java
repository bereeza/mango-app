package backend.mangoapp.controllers;

import backend.mangoapp.entity.Comment;
import backend.mangoapp.entity.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/c")
public class CommentController {
    @PostMapping("/{id}")
    public ResponseEntity<Post> addComment(@PathVariable long id, @RequestBody Comment comment) {
        return null;
    }
}
