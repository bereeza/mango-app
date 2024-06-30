package backend.mangoapp.service.commentService;

import backend.mangoapp.entity.Comment;
import backend.mangoapp.repository.CommentRepository;
import backend.mangoapp.service.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CommentService implements Service<Comment> {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Optional<Comment> getById(long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> getAll() {
        return commentRepository.findAll().stream()
                .sorted(Comparator.comparing(Comment::getCreated_at))
                .toList();
    }

    @Override
    public Comment add(Comment entity) {
        return commentRepository.save(entity);
    }

    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void delete(Comment entity) {
        commentRepository.delete(entity);
    }
}
