package backend.mangoapp.service;

import java.util.List;
import java.util.Optional;

public interface Service<T> {
    Optional<T> getById(long id);

    List<T> getAll();

    T add(T entity);

    void deleteById(long id);

    void delete(T entity);
}
