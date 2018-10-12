package kr.keumyoung.mukin.util;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  on 3/20/2016.
 */
public class CommonHelper {

    public static boolean EmailValidator(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) isValid = true;
        return isValid;
    }

    public static boolean PasswordValidator(String password) {
        return !password.isEmpty();
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getYearList() {
        ArrayList<String> yearList = new ArrayList<>();
        for (int year = 0; year < 40; year++) yearList.add(Integer.toString(1990 + year));
        return yearList;
    }

    public static ArrayList<String> getMonthList() {
        ArrayList<String> monthList = new ArrayList<>();
        monthList.add("Jan");
        monthList.add("Feb");
        monthList.add("Mar");
        monthList.add("Apr");
        monthList.add("May");
        monthList.add("Jun");
        monthList.add("Jul");
        monthList.add("Aug");
        monthList.add("Sep");
        monthList.add("Oct");
        monthList.add("Nov");
        monthList.add("Dec");
        return monthList;
    }

    public static ArrayList<String> getDayList() {
        ArrayList<String> dayList = new ArrayList<>();
        for (int day = 0; day < 31; day++) dayList.add(Integer.toString(day + 1));
        return dayList;
    }

    public static ArrayList<String> getHourList() {
        ArrayList<String> hourList = new ArrayList<>();
        for (int hour = 1; hour <= 12; hour++) {
            if (hour < 10) hourList.add("0" + Integer.toString(hour));
            else hourList.add(Integer.toString(hour));
        }
        return hourList;
    }

    public static ArrayList<String> getMinuteList() {
        ArrayList<String> minuteList = new ArrayList<>();
        for (int hour = 0; hour < 60; hour++) {
            if (hour < 10) minuteList.add("0" + Integer.toString(hour));
            else minuteList.add(Integer.toString(hour));
        }
        return minuteList;
    }


    public static ArrayList<String> getAmPmList() {
        ArrayList<String> dayList = new ArrayList<>();
        dayList.add("AM");
        dayList.add("PM");
        return dayList;
    }

    public static int getMaximum(ArrayList<Integer> values) {
        int maximum = 0;
        for (Integer value : values) {
            if (value > maximum) maximum = value;
        }
        return maximum;
    }

    public static boolean hasGpsModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasNetworkModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }
        return false;
    }
}
