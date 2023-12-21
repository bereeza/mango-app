package backend.mangoapp.service;

import java.util.List;

public interface Service<T> {
    T getById(long id);

    List<T> getAll();

    T add(T entity);

    void deleteById(long id);

    void delete(T entity);
}
