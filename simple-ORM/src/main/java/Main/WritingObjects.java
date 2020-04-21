package Main;

import model.Person;
import orm.EntityManager;

import java.sql.SQLException;

public class WritingObjects {

    public static void main(String[] args) throws SQLException, IllegalAccessException {
        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        Person karim = new Person("Karim", 23);
        Person nouhayla = new Person("Nouhayla", 23);

        System.out.println("****** Before persisting entities ******");
        System.out.println(karim);
        System.out.println(nouhayla);

        entityManager.persist(karim);
        entityManager.persist(nouhayla);

        System.out.println("******* After persisting *********");
        System.out.println(karim);
        System.out.println(nouhayla);
    }
}
