package generic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public void persist(T entity) {
        runInTransaction(entityManager -> {
            entityManager.merge(entity);
        });
    }

    @Override
    public void remove(T entity) {
        runInTransaction(entityManager -> {
            final T managedEntity = entityManager.find(type, entity.getId());
            if (managedEntity != null) {
                entityManager.remove(managedEntity);
            }
        });
    }

    private <R> R run(Function<EntityManager, R> function) {
        final EntityManager entityManager = emf.createEntityManager();
        try {
            return function.apply(entityManager);
        } finally {
            entityManager.close();
        }
    }

    private void run(Consumer<EntityManager> function) {
        run(entityManager -> {
            function.accept(entityManager);
            return null;
        });
    }

    private <R> R runInTransaction(Function<EntityManager, R> function) {
        return run(entityManager -> {
            entityManager.getTransaction().begin();

            final R result = function.apply(entityManager);

            entityManager.getTransaction().commit();

            return result;
        });
    }

    private void runInTransaction(Consumer<EntityManager> function) {
        runInTransaction(entityManager -> {
            function.accept(entityManager);
            return null;
        });
    }
}
