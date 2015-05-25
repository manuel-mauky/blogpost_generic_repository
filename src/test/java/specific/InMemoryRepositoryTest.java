package specific;

import generic.Repository;

public class InMemoryRepositoryTest extends AbstractRepositoryTestBase {

    @Override
    public Repository<Person> getRepository() {
        return new PersonInMemoryRepository();
    }
}
