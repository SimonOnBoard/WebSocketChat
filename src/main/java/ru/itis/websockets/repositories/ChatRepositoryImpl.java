package ru.itis.websockets.repositories;

import org.springframework.stereotype.Repository;
import ru.itis.websockets.orm.Chat;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ChatRepositoryImpl implements ChatRepository {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<Chat> find(String id) {
        return Optional.ofNullable(entityManager.find(Chat.class,id));
    }

    @Override
    public void save(Chat chat) {
        entityManager.persist(chat);
    }

    @Override
    public Optional<Chat> findByCode(String code) {
        TypedQuery<Chat> query = entityManager.createQuery("select pu from Chat pu where pu.code = :code", Chat.class);
        query.setParameter("code", code);
        List<Chat> results = query.getResultList();
        Chat returnObject = null;
        if (!results.isEmpty()) {
            returnObject = results.get(0);
        }
        return Optional.ofNullable(returnObject);
    }
}
