package generic;

import specific.Person;
import specific.PersonJpaRepository;

public class JpaRepositoryTest extends AbstractRepositoryTestBase {

    @Override
    public Repository<Person> getRepository() {

        String pu = "inmemory";
        final PersonJpaRepository repository = new PersonJpaRepository(pu);

        repository.runInTransaction(entityManager -> {
            // HSQLDB specific statement to clear the database
            entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();
        });

        return repository;
    }
}
