package backend.mangoapp.service.userService;

import backend.mangoapp.entity.User;
import backend.mangoapp.repository.UserRepository;
import backend.mangoapp.service.Service;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Service
@AllArgsConstructor
public class UserService implements Service<User> {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User add(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found! Check email.")));
    }

}