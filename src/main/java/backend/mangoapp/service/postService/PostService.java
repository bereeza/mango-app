package backend.mangoapp.service.postService;

import backend.mangoapp.entity.Comment;
import backend.mangoapp.entity.Post;
import backend.mangoapp.repository.CommentRepository;
import backend.mangoapp.repository.PostRepository;
import backend.mangoapp.service.Service;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class PostService implements Service<Post> {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Optional<Post> getById(long id) {
        return postRepository.findById(id);
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public Post add(Post entity) {
        if (entity.getId() != 0) {
            Comment comment = new Comment();
            comment.setId(entity.getId());
            commentRepository.save(comment);
        }
        return postRepository.save(entity);
    }

    @Override
    public void deleteById(long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void delete(Post entity) {
        postRepository.delete(entity);
    }
}
