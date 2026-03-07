package com.mycompany.motorphpayroll.DAO;
import java.util.*;

public interface DAO<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    void save(T entity) throws Exception;
    boolean update(T entity) throws Exception;
    boolean delete(ID id) throws Exception;
}