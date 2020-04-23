package ru.itis.websockets.repositories;

import org.springframework.stereotype.Repository;
import ru.itis.websockets.orm.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class UsersRepositoryImpl implements UsersRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    @Transactional
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Transactional
    @Override
    public Optional<User> findByLogin(String login) {
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.login = :login", User.class);
        query.setParameter("login",login);
        List<User> results = query.getResultList();
        User returnObject = null;

        if (!results.isEmpty()) {
            returnObject = results.get(0);
        }
        return Optional.ofNullable(returnObject);
    }

    @Override
    public Optional<User> findByCode(String key) {
        return Optional.empty();
    }

    @Override
    public void update(User user) {

    }
}
