package backend.mangoapp.controllers;

import backend.mangoapp.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/p")
@RequiredArgsConstructor
public class PostController {
    @GetMapping("/{id}")
    public ResponseEntity<List<Post>> getUserPosts(@PathVariable long id) {
        return null;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Post> addPost(@PathVariable long id,
                                        @RequestBody Post post) {
        return null;
    }

    @DeleteMapping("/{id}/post/{postId}")
    public String deletePost(@PathVariable long id, @PathVariable long postId) {
        return null;
    }

    @PutMapping("/{id}/update/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable long id,
                                           @PathVariable long postId,
                                           @RequestBody Post updatedPost) {
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable long id) {
        return null;
    }
}
