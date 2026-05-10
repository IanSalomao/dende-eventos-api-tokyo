package br.com.softhouse.dende.repositories.util;

import java.util.List;

public interface CrudRepository<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    void update(T entity);
    void delete(ID id);
}