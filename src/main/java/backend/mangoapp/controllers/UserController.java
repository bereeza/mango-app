package backend.mangoapp.controllers;

import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.service.postService.PostService;
import backend.mangoapp.service.userService.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/u")
public class UserController {
    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Post>> getAllPersonalPost(@PathVariable long id) {
        List<Post> personalPosts = postService.getAll().stream()
                .filter(x -> x.getUser().getId() == id)
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(personalPosts);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Post> addPost(@PathVariable long id,
                                        @RequestBody Post post) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.add(post));
    }

    @DeleteMapping("/{id}/post/{postId}")
    public String deletePost(@PathVariable long id, @PathVariable long postId) {
        User user = userService.getById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        if (user.getPosts().stream().anyMatch(post -> post.getId() == postId)) {
            postService.deleteById(postId);
            return "Post deleted";
        } else {
            return "redirect:/error";
        }
    }

    @PutMapping("/{id}/update/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable long id,
                                           @PathVariable long postId,
                                           @RequestBody Post updatedPost) {
        User user = userService.getById(id).orElseThrow(() -> new RuntimeException("User not found!"));
        Post existingPost = user.getPosts().stream()
                .filter(post -> post.getId() == postId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Post not found for the user!"));

        existingPost.setDescription(updatedPost.getDescription());
        Post savedPost = postService.add(existingPost);
        return ResponseEntity.ok(savedPost);
    }
}
