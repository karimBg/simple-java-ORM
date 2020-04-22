package Main;

import model.Person;
import orm.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class UpdateObjects {

    public static void main(String[] args) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        Person karimFound = entityManager.find(Person.class, 1L);
        Person nouhaylaFound = entityManager.find(Person.class, 2L);

        System.out.println("****** Before updating entities ******");
        System.out.println(karimFound);
        System.out.println(nouhaylaFound);

        karimFound.setAge(55);
        karimFound.setName("karimmmmm");

        nouhaylaFound.setAge(20);
        nouhaylaFound.setName("Dr. nouhayla");

        entityManager.update(karimFound);
        entityManager.update(nouhaylaFound);
        System.out.println("****** After updating entities ******");
        System.out.println(karimFound);
        System.out.println(nouhaylaFound);
    }
}
