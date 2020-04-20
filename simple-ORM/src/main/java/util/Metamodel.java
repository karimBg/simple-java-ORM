package util;

import annotations.Column;
import annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Metamodel<T> {

    private Class<T> clss;

    public Metamodel(Class<T> clss) {
        this.clss = clss;
    }

    public static <T> Metamodel<T> of(Class<T> clss) {
        return new Metamodel(clss);
    }

    public PrimaryKeyField getPrimaryKey() {
        Field[] declaredFields = clss.getDeclaredFields();
        for(Field field: declaredFields) {
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if(primaryKey != null) {
                PrimaryKeyField primaryKeyField = new PrimaryKeyField(field);
                return primaryKeyField;
            }
        }
        // if no PrimaryKey field is found just throw an exception
        throw new IllegalArgumentException("No PrimaryKey found in class " + clss.getSimpleName());
    }

    public List<ColumnField> getColums() {
        // create an empty list to store the column fields
        List<ColumnField> columnFieldList = new ArrayList<ColumnField>();

        Field[] declaredFields = clss.getDeclaredFields();
        for(Field field: declaredFields) {
            Column column = field.getAnnotation(Column.class);
            if(column != null) {
                ColumnField columnField = new ColumnField(field);
                columnFieldList.add(columnField);
            }
        }
        // if no column fields where found the returned list will be empty
        return columnFieldList;
    }
}
