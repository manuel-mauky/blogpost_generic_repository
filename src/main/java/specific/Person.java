package specific;

import generic.Identity;

import javax.persistence.Entity;

@Entity
public class Person extends Identity {

    private String name;

    Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
