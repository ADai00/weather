package edu.hnie.weather.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    /**
     * 判断当前日期是星期几
     *
     * @param pTime
     * @return
     * @throws Throwable
     */
    public static String dayForWeek(String pTime)  {
        String result ;
        Calendar cal = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            Date tmpDate = format.parse(pTime);

            cal = new GregorianCalendar();

            cal.setTime(tmpDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int flag = cal.get(Calendar.DAY_OF_WEEK);
        switch (flag){
            case 1:
                result = "星期日";
                return result;
            case 2:
                result = "星期一";
                return result;
            case 3:
                result = "星期二";
                return result;
            case 4:
                result = "星期三";
                return result;
            case 5:
                result = "星期四";
                return result;
            case 6:
                result = "星期五";
                return result;
            case 7:
                result = "星期六";
                return result;
        }
        return null;
    }
}
