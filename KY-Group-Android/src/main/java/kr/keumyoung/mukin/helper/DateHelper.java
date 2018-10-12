package kr.keumyoung.mukin.helper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;

/**
 *  on 19/08/17.
 */

public class DateHelper {

    @Inject
    public DateHelper() {
    }

    public  String getDuration(int duration) {
        LocalTime time = new LocalTime(0, 0);
        time = time.plusSeconds(duration);
        return DateTimeFormat.forPattern("m:ss").print(time);
    }

    public  String getDate(DateTime date) {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy");
        return dtfOut.print(date);
    }

    public  String getDateTime(DateTime dateTime) {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        return dtfOut.print(dateTime);
    }

    public DateTime parseDate(String serverDateString) {
        if (serverDateString.equalsIgnoreCase("null")) return new DateTime(DateTimeZone.getDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.getDefault());
        return dateTimeFormatter.parseDateTime(serverDateString);
    }

    public  String getFormattedDate(long date) {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return dtfOut.print(date);
    }

    public  String getFormattedDate(DateTime date) {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return dtfOut.print(date);
    }

    public  String getDateTimeData(DateTime dateTime) {
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        return dtfOut.print(dateTime);
    }
}
