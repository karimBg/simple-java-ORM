import model.Person;
import util.ColumnField;
import util.Metamodel;
import util.PrimaryKeyField;

import java.lang.reflect.Field;
import java.util.List;

public class MetamodelTest {

    public static void main(String[] args) {
        Metamodel metamodel = Metamodel.of(Person.class);

        PrimaryKeyField primaryKeyField = metamodel.getPrimaryKey();
        System.out.println("Primary Key name: " + primaryKeyField.getName() +
                ", Primary Key type: " + primaryKeyField.getType().getSimpleName());

        List<ColumnField> columnFields = metamodel.getColums();
        for(ColumnField columnField: columnFields) {
            System.out.println("Column name: " + columnField.getName() +
                    ", Column type: " + columnField.getType().getSimpleName());
        }
    }
}
