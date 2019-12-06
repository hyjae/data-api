package kr.datasolution.ds.api.util;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    // TODO: without @Column?
    public static List<String> getColumnNames(Class clazz)
    {
        List<String> Columns = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column col = field.getAnnotation(Column.class);
            if (col != null)
                Columns.add(col.name());
        }
        return Columns;
    }
}
