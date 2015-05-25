package specific;

import generic.Repository;

public class JpaRepositoryTest extends AbstractRepositoryTestBase {

    @Override
    public Repository<Person> getRepository() {

        String pu = "inmemory";
        return new PersonJpaRepository(pu);
    }
}
