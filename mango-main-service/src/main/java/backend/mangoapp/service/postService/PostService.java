package backend.mangoapp.service.postService;

import backend.mangoapp.entity.Post;
import backend.mangoapp.repository.PostRepository;
import backend.mangoapp.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class PostService implements Service<Post> {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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
        entity.setComments(new ArrayList<>());
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
