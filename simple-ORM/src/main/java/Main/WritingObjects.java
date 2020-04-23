package Main;

import beanmanager.BeanManager;
import model.Person;
import orm.EntityManager;
import orm.ManagedEntityManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class WritingObjects {

    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {

        // To create an EntityManager we will use a new object called: BeanManager
        // We will use a singleton to instantiate this beanManager object
        BeanManager beanManager = BeanManager.getInstance();

        // From the beanManager we can create an instance of: EntityManager
        EntityManager<Person> entityManager = beanManager.getInstance(ManagedEntityManager.class);

        // Then we can use our API as before:
        Person karim = entityManager.find(Person.class, 1L);
        Person nouhayla = entityManager.find(Person.class, 2L);

        System.out.println(karim);
        System.out.println(nouhayla);

    }
}
