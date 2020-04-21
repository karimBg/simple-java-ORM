package Main;

import model.Person;
import orm.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class ReadingObjects {

    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        Person karim = entityManager.find(Person.class, 1L);
        Person nouhayla = entityManager.find(Person.class, 2L);

        System.out.println("******* Result from reading the DB *********");
        System.out.println(karim);
        System.out.println(nouhayla);
    }
}
