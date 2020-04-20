package orm;

import util.Metamodel;

import java.sql.PreparedStatement;

public class EntityMangerImpl<T> implements EntityManager<T> {

    public void persist(T t) {
        Metamodel metamodel = Metamodel.of(t.getClass());
        String sql = metamodel.buildInsertRequest();
        PreparedStatement statement = prepareStatementWith(sql).andParameters(t);
        return statement.executeUpdate();
    }
}
