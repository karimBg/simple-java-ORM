package Main;

import model.Person;
import orm.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class RemoveObject {

    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {

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

        Person karimFound = entityManager.find(Person.class, 1L);
        Person nouhaylaFound = entityManager.find(Person.class, 2L);

        System.out.println("****** Before removing entities ******");
        System.out.println(karimFound);
        System.out.println(nouhaylaFound);

        entityManager.remove(karimFound);
        entityManager.remove(nouhaylaFound);

        Person karimFound2 = entityManager.find(Person.class, 1L);
        Person nouhaylaFound2 = entityManager.find(Person.class, 2L);

    }
}
