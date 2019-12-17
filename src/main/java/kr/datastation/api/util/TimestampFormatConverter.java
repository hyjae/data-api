package kr.datastation.api.util;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Component("timestampConverter")
public class TimestampFormatConverter {

    public String convert(Timestamp timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(timestamp.getTime());
        return format.format(date);
    }
}

