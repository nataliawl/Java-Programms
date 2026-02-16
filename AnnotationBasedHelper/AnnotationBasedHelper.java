import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class AnnotationBasedHelper implements  SQLiteHelper {

    public String getTypeOfField(Field checkField){
        String typeName = checkField.getType().getName();
        return switch (typeName) {
            case "java.lang.String" , "String" -> "TEXT";
            case "java.lang.Integer", "java.lang.Long", "int", "long" -> "INTEGER";
            case "java.lang.Double", "java.lang.Float", "double", "float" -> "REAL";
            default -> null;
        };
    }

    public boolean checkIfChangeIntoColumn(Field checkField){
        if(!checkField.isAnnotationPresent(SQL.class)) return false;
        if(getTypeOfField(checkField) == null) return false;
        return true;
    }

    public String createTable(String tableName,List<String> columnsType, List<String> columnsName ){
        StringBuilder SQLTable = new StringBuilder("CREATE TABLE " + tableName + " (\n" );
        for(int j = 0; j < columnsType.size(); j++){
            if( j == columnsType.size() - 1){
                SQLTable.append(columnsName.get(j)).append(" ").append(columnsType.get(j)).append("\n);");
                break;
            }
            SQLTable.append(columnsName.get(j)).append(" ").append(columnsType.get(j)).append(",\n");
        }
        return SQLTable.toString();
    }

    @Override
    public String toSQL(Object object, String tableName) {
        List<String> columnsType = new ArrayList<>();
        List<String> columnsName = new ArrayList<>();
        int i = 0;

        Class<?> objectClass = object.getClass();
        Field[] objectFields = objectClass.getFields();

        for (Field objectField : objectFields) {
            if(!checkIfChangeIntoColumn(objectField)) continue;
            columnsType.add(i, getTypeOfField(objectField));
            columnsName.add(i, objectField.getName());
            i++;
        }

        return createTable(tableName, columnsType, columnsName);
    }
}

