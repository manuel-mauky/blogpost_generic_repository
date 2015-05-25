package generic;

import specific.Person;
import specific.PersonInMemoryRepository;

public class InMemoryRepositoryTest extends AbstractRepositoryTestBase {

    @Override
    public Repository<Person> getRepository() {
        return new PersonInMemoryRepository();
    }
}
