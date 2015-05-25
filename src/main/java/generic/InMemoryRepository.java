package generic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class InMemoryRepository<T extends Identity> implements Repository<T> {

    private Set<T> entities = new HashSet<>();

    @Override
    public Set<T> get() {
        return Collections.unmodifiableSet(entities);
    }

    @Override
    public void persist(T entity) {
        entities.add(entity);
    }

    @Override
    public void remove(T entity) {
        entities.remove(entity);
    }
}