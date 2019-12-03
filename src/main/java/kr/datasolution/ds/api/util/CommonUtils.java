package kr.datasolution.ds.api.util;

import javax.persistence.Tuple;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    static public String tupleToCsvFormat(Tuple tupleData) {
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
}
