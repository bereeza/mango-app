package backend.mangoapp.controllers;

import backend.mangoapp.entity.Comment;
import backend.mangoapp.entity.Post;
import backend.mangoapp.entity.User;
import backend.mangoapp.service.commentService.CommentService;
import backend.mangoapp.service.postService.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final PostService postService;
    private final CommentService commentService;
    private final HttpSession session;

    @Autowired
    public HomeController(PostService postService,
                          CommentService commentService,
                          HttpSession session) {
        this.postService = postService;
        this.commentService = commentService;
        this.session = session;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(postService.getById(id).orElseThrow(() -> new RuntimeException("Post doesn't found.")));
    }

    @PostMapping("/{id}/c")
    public ResponseEntity<Post> addComment(@PathVariable long id, @RequestBody Comment comment) {
        Post post = postService.getById(id).orElseThrow(() -> new RuntimeException("Post doesn't found."));
        User user = (User) session.getAttribute("currentUser");
        post.getComments().add(comment);
        Comment buildComment = Comment.builder()
                .user(user)
                .post(post)
                .created_at(new Timestamp(System.currentTimeMillis()))
                .build();
        commentService.add(buildComment);
        return ResponseEntity.ok(post);
    }
}
