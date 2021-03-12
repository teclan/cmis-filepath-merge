package cmis.teclan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static  final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getDataString(long time) {
        Date date = new Date();
        date.setTime(time);
        return SDF.format(date);
    }
}
