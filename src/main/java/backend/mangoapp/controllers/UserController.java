package backend.mangoapp.controllers;

import backend.mangoapp.entity.Post;
import backend.mangoapp.service.postService.PostService;
import backend.mangoapp.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/u")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<List<Post>> getAllPersonalPost(@PathVariable long id) {
        List<Post> personalPosts = postService.getAll().stream()
                .filter(x -> x.getUser().getId() == id)
                .collect(Collectors.toCollection(ArrayList::new));

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
    public String deletePost(@PathVariable long id,
                             @PathVariable long postId) {
        if (postService.getById(postId).isPresent()) {
            postService.deleteById(postId);
            return "Post deleted";
        } else {
            return "redirect:/error";
        }
    }
}
