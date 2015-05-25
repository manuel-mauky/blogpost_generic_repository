package specific;

import generic.Repository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractRepositoryTestBase {

    Repository<Person> repository;

    Person person1;
    Person person2;
    Person person3;

    public abstract Repository<Person> getRepository();

    @Before
    public void setup() {
        repository = getRepository();
        person1 = new Person("test 1");
        person2 = new Person("test 2");
        person3 = new Person("test 3");
    }

    @Test
    public void testPersist() {
        assertThat(repository.get()).isEmpty();


        repository.persist(person1);
        assertThat(repository.get()).contains(person1);


        repository.persist(person1, person2);
        assertThat(repository.get()).containsOnly(person1, person2);


        List<Person> personList = new ArrayList<>();
        personList.add(person3);


        repository.persist(personList);
        assertThat(repository.get()).containsOnly(person1, person2, person3);
    }

    @Test
    public void testRemove() {
        repository.persist(person1, person2, person3);


        repository.remove(person1);

        assertThat(repository.get()).doesNotContain(person1);


        repository.remove(person2, person3);
        assertThat(repository.get()).isEmpty();



        repository.persist(person1, person2, person3);

        List<Person> personList = new ArrayList<>();
        personList.add(person3);

        repository.remove(personList);
        assertThat(repository.get()).doesNotContain(person3);



        repository.remove(item -> item.getName().endsWith("2"));
        assertThat(repository.get()).doesNotContain(person2).contains(person1);


        repository.remove(person1.getId());
        assertThat(repository.get()).isEmpty();
    }


    @Test
    public void testGet() {
        repository.persist(person1, person2, person3);


        assertThat(repository.get()).contains(person1, person2, person3);


        final Optional<Person> personOptional = repository.get(person2.getId());


        assertThat(personOptional.isPresent()).isTrue();
        assertThat(personOptional.get()).isEqualTo(person2);


        final Optional<Person> personOptional2 = repository.get("some other id");
        assertThat(personOptional2.isPresent()).isFalse();


        final Set<Person> personSet = repository.get(item -> item.getName().startsWith("test"));

        assertThat(personSet).contains(person1, person2, person3);
    }



}
