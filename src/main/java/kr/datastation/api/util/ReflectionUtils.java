package kr.datastation.api.util;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ClassUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    public static List<String> getColumnNames(Class clazz) {
        List<String> Columns = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                Columns.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, col.name()));
            } else {
                Columns.add(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
            }
        }
        return Columns;
    }

    public static List<String> getValNames(Class clazz)
    {
        List<String> Columns = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Columns.add(field.getName());
        }
        return Columns;
    }

    public static String objectToCSVFormat(Object object) {
        return objectToCSVFormatHelper(object) + "\n";
    }

    private static String objectToCSVFormatHelper(Object object) {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            try {
                fields[i].setAccessible(true);
                Object o = fields[i].get(object);
                Class<?> aClass = o.getClass();
                if (ClassUtils.isPrimitiveOrWrapper(aClass) || aClass.getName().equalsIgnoreCase("java.lang.String")) {
                    stringBuilder.append(o.toString());
                } else {
                    stringBuilder.append(objectToCSVFormatHelper(o));
                } if (i != fields.length - 1) {
                    stringBuilder.append(",");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
