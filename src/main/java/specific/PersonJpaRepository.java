package specific;

import generic.JpaRepository;

public class PersonJpaRepository extends JpaRepository<Person> {

    public PersonJpaRepository(String persistenceUnitName) {
        super(Person.class, persistenceUnitName);
    }
}
