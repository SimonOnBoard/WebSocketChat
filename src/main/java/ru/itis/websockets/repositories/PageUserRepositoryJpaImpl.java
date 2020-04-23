package ru.itis.websockets.repositories;

import org.springframework.stereotype.Repository;
import ru.itis.websockets.orm.Chat;
import ru.itis.websockets.orm.PageUser;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PageUserRepositoryJpaImpl implements PageUserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PageUser> findByUUID(String pageId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageUser> criteriaQuery = criteriaBuilder.createQuery(PageUser.class);
        Root<PageUser> root = criteriaQuery.from(PageUser.class);
        criteriaQuery.select(root);

        ParameterExpression<String> params = criteriaBuilder.parameter(String.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("pageId"), params));

        TypedQuery<PageUser> query = entityManager.createQuery(criteriaQuery);
        query.setParameter(params, pageId);
        List<PageUser> queryResult = query.getResultList();
        PageUser returnObject = null;

        if (!queryResult.isEmpty()) {
            returnObject = queryResult.get(0);
        }

        return Optional.ofNullable(returnObject);
    }

    @Override
    public Optional<PageUser> findByUserId(Long id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageUser> criteriaQuery = criteriaBuilder.createQuery(PageUser.class);
        Root<PageUser> root = criteriaQuery.from(PageUser.class);
        criteriaQuery.select(root);

        ParameterExpression<Long> params = criteriaBuilder.parameter(Long.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("userId"), params));

        TypedQuery<PageUser> query = entityManager.createQuery(criteriaQuery);
        query.setParameter(params, id);
        List<PageUser> queryResult = query.getResultList();
        PageUser returnObject = null;

        if (!queryResult.isEmpty()) {
            returnObject = queryResult.get(0);
        }

        return Optional.ofNullable(returnObject);
    }

    @Override
    public Optional<PageUser> findByUserIdAndChat(Chat chat, Long userId) {
        TypedQuery<PageUser> query = entityManager.createQuery("select pu from PageUser pu where pu.userId = :id and pu.chat = :chat", PageUser.class);
        query.setParameter("chat", chat);
        query.setParameter("id", userId);
        List<PageUser> results = query.getResultList();
        PageUser returnObject = null;
        if (!results.isEmpty()) {
            returnObject = results.get(0);
        }
        return Optional.ofNullable(returnObject);
    }

    @Override
    public List<PageUser> findAllByUserId(Long userId) {
        TypedQuery<PageUser> query = entityManager.createQuery("select pu from PageUser pu where pu.userId = :id", PageUser.class);
        query.setParameter("id", userId);
        List<PageUser> results = query.getResultList();
        return results;
    }

    @Override
    public Optional<PageUser> findByPageIdAndChat(Chat chat, String id) {
        TypedQuery<PageUser> query = entityManager.createQuery("select pu from PageUser pu where pu.pageId = :id and pu.chat = :chat", PageUser.class);
        query.setParameter("chat", chat);
        query.setParameter("id", id);
        List<PageUser> results = query.getResultList();
        PageUser returnObject = null;
        if (!results.isEmpty()) {
            returnObject = results.get(0);
        }
        return Optional.ofNullable(returnObject);
    }

    @Override
    public Optional<PageUser> find(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<PageUser> findAll() {
        return null;
    }

    @Override
    public void save(PageUser entity) {
        entityManager.persist(entity);
    }

    @Override
    public void delete(Long aLong) {
        PageUser pageUser = entityManager.find(PageUser.class, aLong);
        entityManager.remove(pageUser);
    }

    @Override
    public void update(PageUser entity) {
        entityManager.merge(entity);
    }
}
