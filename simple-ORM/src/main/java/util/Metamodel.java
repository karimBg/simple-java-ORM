package util;

import annotations.Column;
import annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Metamodel {

    private Class<?> clss;

    public Metamodel(Class<?> clss) {
        this.clss = clss;
    }

    public static Metamodel of(Class<?> clss) {
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

    // INSERT INTO person (id, name, age) VALUES (?, ?, ?);
    public String buildInsertRequest() {

        // build the first part of the sql request: id, name, age
        String columnElements = buildColumnNames();

        // build the second part: ?, ?, ?
        String questionMarkElement = buildQuestionMarksElement();

        return "INSERT INTO " + this.clss.getSimpleName() +
                " (" + columnElements + ") VALUES " + " (" + questionMarkElement + ")";
    }

    private String buildColumnNames() {
        String primaryKeyColumnName = getPrimaryKey().getName();
        List<String> columnNames = getColums()
                .stream()
                .map(ColumnField::getName)
                .collect(Collectors.toList());
        columnNames.add(0, primaryKeyColumnName);
        return String.join(", ", columnNames);
    }

    private String buildQuestionMarksElement() {
        int numberOfColumns = getColums().size() + 1;
        return IntStream
                .range(0, numberOfColumns)
                .mapToObj(index -> "?")
                .collect(Collectors.joining(", "));
    }

}
