package orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2EntityManager<T> extends AbstractEntityManager<T> {

    public Connection buildConnection() throws SQLException {
        String username = "";
        String password = "";
        String url = "jdbc:h2:C:\\Users\\mkboughammoura\\Desktop\\orm-project\\simple-java-ORM\\simple-ORM\\db-files\\db-orm";
        return DriverManager.getConnection(url, username, password);
    }
}
