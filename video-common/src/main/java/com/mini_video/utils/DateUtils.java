package com.mini_video.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: Canyon
 * @Description:
 * @Date: Created in 11:28 2017/10/21
 * @Modified By:
 */
public class DateUtils {

    private static Log log = LogFactory.getLog(DateUtils.class);

    private static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss";

    public static String getDatePattern() {
        return defaultDatePattern;
    }

    public static String getToday(String pattern) {
        Date today = new Date();
        return format(today, pattern);
    }

    public static String format(Date date) {
        return date == null ? "" : format(date, getDatePattern());
    }

    public static String format(Date date, String pattern) {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String strDate) {
        return StringUtils.isEmpty(strDate) ? null : parse(strDate, getDatePattern());
    }

    public static Date parse(String strDate, String pattern) {
        try {
            return StringUtils.isEmpty(strDate) ? null : new SimpleDateFormat(pattern).parse(strDate);
        } catch (ParseException e) {
            log.error(e.toString(), e);
            return null;
        }
    }

    public static int between(String begin, String end) {
        long interval = 0;
        try {

            Date a = parse(begin);
            Date b = parse(end);
            interval = (b.getTime() - a.getTime()) / 1000;

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return Integer.parseInt(String.valueOf(interval));
    }

    /**
     * 判断选择的日期是否是本月
     */
    public static boolean isThisMonth(String date) {
        return isThisTime(date, "yyyy-MM");
    }

    private static boolean isThisTime(String time, String pattern) {
        Date date = parse(time, pattern);
        //参数时间
        String param = format(date, pattern);
        //当前时间
        String now = format(new Date(), pattern);
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    /**
     * 指定日期后多少天
     *
     * @param num
     * @return
     */
    public static Date plusDay(int num, Date d) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ca = Calendar.getInstance();
        ca.setTime(d);
//        int day = ca.get(Calendar.DATE);
        // num为增加的天数
        ca.add(Calendar.DATE, num);
        d = ca.getTime();
        String enddate = format.format(d);

        return parse(enddate);
    }

    public static Date plusYear(Date date, Integer num) {
        SimpleDateFormat format = new SimpleDateFormat(defaultDatePattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, num);
        date = cal.getTime();
        String endDate = format.format(date);
        return parse(endDate);
    }

    /**
     * 隔多少天，未精确到小时
     *
     * @param start
     * @param end
     * @return
     */
    public static int betWeenDays(Date start, Date end) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(start);
        //日期start 在本年中的第几天
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(end);
        //日期end 在本年中的第几天
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;
    }

    /**
     * @param endTimeData 时间数据对象
     * @return 是否还可以用, 没有过期可以使用, 过期了就不能用了
     * @author canyon
     **/
    public static Boolean compareDate(String endTimeData) {

        Boolean isExpire = true;
        if (!StringUtil.isBlank(endTimeData)) {
            Date endTime = DateUtils.parse(endTimeData, "yyyy-MM-dd");
            Date nowTime = DateUtils.parse(DateUtils.format(new Date()), "yyyy-MM-dd");
            if (endTime.getTime() >= nowTime.getTime()) {
                isExpire = false;
            }
        }
        return isExpire;
    }


    /**
     * 生成随机时间
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static Date randomDate(String beginDate, String endDate) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            //getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。 
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        //如果返回的是开始时间和结束时间，则递归调用本函数查找随机值 
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;

    }


    public static void main(String[] args) {
        Date year = DateUtils.plusYear(new Date(), 1);
        System.out.println(year);
        System.out.println(DateUtils.format(year));

        String begin = "2016-07-27 22:34:26";
        String end = "2016-07-28 21:34:26";

        Integer i = DateUtils.between("2016-07-28 20:34:26", "2016-07-28 21:34:26");
        System.out.println(DateUtils.format(new Date(), "yyyy年MM月dd日"));
        Integer j = DateUtils.betWeenDays(new Date(), DateUtils.parse(end));
        System.out.println(j);
        Date k = DateUtils.plusDay(5, DateUtils.parse(begin));
        System.out.println(k);

        Date randomDate=randomDate("2018-05-01","2018-05-31");

        System.out.println(format(randomDate));


    }

}
