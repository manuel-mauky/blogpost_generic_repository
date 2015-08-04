package generic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class JpaRepository<T extends Identity> implements Repository<T> {

    private EntityManagerFactory emf;

    private Class<T> type;

    public JpaRepository(Class<T> type, String persistenceUnitName) {
        this.type = type;
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    }


    @Override
    public Set<T> get() {
        List<T> resultList = run(entityManager -> {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<T> criteria = criteriaBuilder.createQuery(type);

            final Root<T> root = criteria.from(type);
            criteria.select(root);

            final TypedQuery<T> query = entityManager.createQuery(criteria);
            return query.getResultList();
        });

        return new HashSet<>(resultList);
    }


    @Override
    public Optional<T> get(String id) {
        return Optional.ofNullable(run(entityManager -> {
            return entityManager.find(type, id);
        }));
    }


    @Override
    public void persist(T entity) {
        runInTransaction(entityManager -> {
            entityManager.merge(entity);
        });
    }

    @Override
    public void persist(Collection<T> entities) {
        runInTransaction(entityManager -> {
            entities.forEach(entityManager::merge);
        });
    }

    @Override
    public void remove(T entity) {
        remove(entity.getId());
    }

    @Override
    public void remove(String id) {
        runInTransaction(entityManager -> {
            final T managedEntity = entityManager.find(type, id);
            if (managedEntity != null) {
                entityManager.remove(managedEntity);
            }
        });
    }

    @Override
    public void remove(Collection<T> entities) {
        runInTransaction(entityManager -> {
            entities
                    .stream()
                    .map(T::getId)
                    .map(id -> entityManager.find(type, id))
                    .filter(Objects::nonNull)
                    .forEach(entityManager::remove);
        });
    }

    @Override
    public void remove(Predicate<T> predicate) {
        remove(get(predicate));
    }

    protected <R> R run(Function<EntityManager, R> function) {
        final EntityManager entityManager = emf.createEntityManager();
        try {
            return function.apply(entityManager);
        } finally {
            entityManager.close();
        }
    }

    protected void run(Consumer<EntityManager> function) {
        run(entityManager -> {
            function.accept(entityManager);
            return null;
        });
    }

    protected <R> R runInTransaction(Function<EntityManager, R> function) {
        return run(entityManager -> {
            entityManager.getTransaction().begin();

            final R result = function.apply(entityManager);

            entityManager.getTransaction().commit();

            return result;
        });
    }

    protected void runInTransaction(Consumer<EntityManager> function) {
        runInTransaction(entityManager -> {
            function.accept(entityManager);
            return null;
        });
    }
}
