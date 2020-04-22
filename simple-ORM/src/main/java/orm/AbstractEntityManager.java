package orm;

import util.ColumnField;
import util.Metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractEntityManager<T> implements EntityManager<T> {

    private AtomicLong idGenerator = new AtomicLong(0L);

    public abstract Connection buildConnection() throws SQLException;

    @Override
    public void persist(T t) throws SQLException, IllegalAccessException {
        Metamodel metamodel = Metamodel.of(t.getClass());
        String sql = metamodel.buildInsertRequest();
        try(PreparedStatement statement = prepareStatementWith(sql).andParameters(t)) {
            statement.executeUpdate();
        }
    }

    @Override
    public T find(Class<T> clss, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Metamodel metamodel = Metamodel.of(clss);
        String sql = metamodel.buildSelectRequest();
        try(PreparedStatement statement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
            ResultSet resultSet = statement.executeQuery()) {
            return buildInstanceFrom(clss, resultSet);
        }
    }

    private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {
        // Specif H2 information to connect to the H2 DB.
        Connection connection = buildConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        return new PreparedStatementWrapper(statement);
    }

    private T buildInstanceFrom(Class<T> clss, ResultSet resultSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        Metamodel metamodel = Metamodel.of(clss);

        // Build an instance of type clss
        T t = clss.getConstructor().newInstance();

        // Populate the primaryKey field
        Field primaryKeyField = metamodel.getPrimaryKey().getField();
        String primaryKeyFieldName = metamodel.getPrimaryKey().getName();
        Class<?> primaryKeyFieldType = metamodel.getPrimaryKey().getType();

        // Tell the resultSet to advance on the table of results
        resultSet.next();

        if(primaryKeyFieldType == long.class) {
            // Inside the DB the Primary Key was stored as an Int
            int primaryKeyValue = resultSet.getInt(primaryKeyFieldName);

            // Populate the instance with the value of the primaryKey
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(t, primaryKeyValue);
        }

        // Populate the rest of the fields
        for(ColumnField columnField: metamodel.getColums()) {
            Field field = columnField.getField();
            String columnFieldName = columnField.getName();
            Class<?> columnFiedType = columnField.getType();

            field.setAccessible(true);

            if(columnFiedType == int.class) {
                int columnValue = resultSet.getInt(columnFieldName);
                field.set(t, columnValue);
            } else if(columnFiedType == String.class) {
                String columnValue = resultSet.getString(columnFieldName);
                field.set(t, columnValue);
            }
        }
        return t;
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

        // Provide a value for the Primary key to be used by the WHERE clause of the SELECT query
        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
            // We will only care about the long type
            if(primaryKey.getClass() == Long.class) {
                statement.setLong(1, (Long)primaryKey);
            }
            return statement;
        }
    }
}
