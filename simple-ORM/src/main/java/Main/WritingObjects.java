package Main;

import model.Person;
import orm.EntityManager;

public class WritingObjects {

    public static void main(String[] args) {
        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        Person karim = new Person("Karim", 23);
        Person nouhayla = new Person("Nouhayla", 23);

        entityManager.persist(karim);
        entityManager.persist(nouhayla);
    }
}
