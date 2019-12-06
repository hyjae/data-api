package kr.datasolution.ds.api.util;

import javax.persistence.Tuple;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommonUtils {

    static public Date convertToDate(String date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            // TODO:
            e.printStackTrace();
            return null;
        }
    }

    static public String tupleToCSVFormat(Tuple tupleData) {
        StringBuilder row = new StringBuilder();
        final int length = tupleData.toArray().length;

        for (int i = 0; i < length; ++i) {
            try {
                row.append(tupleData.get(i).toString());
            } catch(NullPointerException e) {
                row.append("NULL");
            }
            if (i != length - 1)
                row.append(", ");
        }
        return row.toString();
    }

    // TODO: generic
    static public String listToCSVFormat(List<?> arrayList) {
        StringBuilder row = new StringBuilder();
        final int length = arrayList.toArray().length;

        for (int i = 0; i < length; ++i) {
            try {
                row.append(arrayList.get(i).toString());
            } catch(NullPointerException e) {
                row.append("NULL");
            }
            if (i != length - 1)
                row.append(", ");
        }
        return row.toString();
    }

    public static boolean isValidDateFormat(String dateFormat, String date) {
        final int length = dateFormat.length();
        if (!isNumeric(date) || (date.length() != length))
            return false;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            Date parse = simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null)
            return false;
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
