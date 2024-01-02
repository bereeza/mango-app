package backend.mangoapp.controllers;

import backend.mangoapp.entity.Post;
import backend.mangoapp.service.postService.PostService;
import backend.mangoapp.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/u")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @PostMapping("/{id}/posts")
    public ResponseEntity<Post> addPost(@RequestParam long id,
                                        @RequestBody Post post) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.add(post));
    }

    @DeleteMapping("/{id}/post/{postId}")
    public String deletePost(@RequestParam long id,
                             @RequestParam long postId) {
        if (postService.getById(postId).isPresent()) {
            postService.deleteById(postId);
            return "Post deleted";
        } else {
            return "redirect:/error";
        }
    }
}
