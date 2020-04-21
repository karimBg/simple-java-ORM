package orm;

import util.ColumnField;
import util.Metamodel;
import util.PrimaryKeyField;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class EntityMangerImpl<T> implements EntityManager<T> {

    private AtomicLong idGenerator = new AtomicLong(0L);

    public void persist(T t) throws SQLException, IllegalAccessException {
        Metamodel metamodel = Metamodel.of(t.getClass());
        String sql = metamodel.buildInsertRequest();
        PreparedStatement statement = prepareStatementWith(sql).andParameters(t);
        statement.executeUpdate();
    }

    private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {
        // Specif H2 information to connect to the H2 DB.
        String username = "";
        String password = "";
        String url = "jdbc:h2:C:\\Users\\mkboughammoura\\Desktop\\orm-project\\simple-java-ORM\\simple-ORM\\db-files\\db-orm";
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql);
        return new PreparedStatementWrapper(statement);
    }

    private class PreparedStatementWrapper {

        private PreparedStatement statement;

        public PreparedStatementWrapper(PreparedStatement statement) {
            this.statement = statement;
        }

        // We need to provide a value for each parameter: id, name, age
        public PreparedStatement andParameters(T t) throws SQLException, IllegalAccessException {
            Metamodel metamodel = Metamodel.of(t.getClass());
            Class<?> primaryKeyType = metamodel.getPrimaryKey().getType();
            // In this project we will deal with only primaryKey of type: long
            if(primaryKeyType == long.class) {
                // We will leave it to the EntityManager to generate the id field value
                long id = idGenerator.incrementAndGet();
                statement.setLong(1, id);
                // Set the value in the object itself
                // Get the primary key field
                Field primaryKeyField = metamodel.getPrimaryKey().getField();
                primaryKeyField.setAccessible(true);
                primaryKeyField.set(t, id);
            }

            // Handle the rest of the fields
            for(int columnIndex = 0; columnIndex < metamodel.getColums().size(); columnIndex++) {
                // Get the columnField by it's index number
                ColumnField columnField = metamodel.getColums().get(columnIndex);
                // Get the field type
                Class<?> fieldType = columnField.getType();
                // Get the field value
                Field field = columnField.getField();
                // Set the field as accessible because it will most likely be private
                field.setAccessible(true);
                // Get the field value
                Object fieldValue = field.get(t);
                // set the value to the request depending on the field type, we only care about the String and int types
                if(fieldType == int.class) {
                    statement.setInt(columnIndex + 2, (int)fieldValue);
                } else if(fieldType == String.class) {
                    statement.setString(columnIndex + 2, (String)fieldValue);
                }
            }
            return statement;
        }
    }
}
