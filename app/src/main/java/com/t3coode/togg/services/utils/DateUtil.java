package com.t3coode.togg.services.utils;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {
    public static Date parseDate(String dateStr) {
        try {
            JodaDateFormat format = new JodaDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssZZ");
            return format.parse(dateStr);
        } catch (ParseException ex) {
            return new Date();
        }
    }

    public static String dateDifference(Date date1, Date date2) {
        int diffInSeconds = 0;

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        diffInSeconds = (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / 1000);
        if (diffInSeconds < 1) {
            diffInSeconds = 1;
        }

        String[][] conversionMatrix = { { "60", "1", "seconds ago" },
                { "3600", "60", "minutes ago" },
                { "86400", "3600", "hours ago" },
                { "604800", "86400", "days ago" },
                { "2419200", "604800", "weeks ago" },
                { "29030400", "2419200", "months ago" },
                { "2903040000", "29030400", "years ago" }, };

        String[] unitsArray = { "just now", "", "", "yesterday", "last week",
                "last month", "last year" };

        int i = 0;
        while (diffInSeconds >= Long.parseLong(conversionMatrix[i][0])) {
            i++;
        }

        int roundedDiff = Math.round(diffInSeconds
                / Integer.parseInt(conversionMatrix[i][1]));

        if ((roundedDiff == 1 || roundedDiff == 0) && !unitsArray[i].equals("")) {
            return unitsArray[i];
        } else {
            return roundedDiff + " " + conversionMatrix[i][2];
        }

    }

    public static class JodaDateFormat extends SimpleDateFormat {

        /**
         * 
         */
        private static final long serialVersionUID = -3067686477822431375L;
        private static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        private DateTimeFormatter mFormatter;
        private DateTimeFormatter mDefaultFormatter;

        public JodaDateFormat(String pattern) {
            this.mFormatter = DateTimeFormat.forPattern(pattern);
            this.mDefaultFormatter = DateTimeFormat.forPattern(DEFAULT_FORMAT);
        }

        public JodaDateFormat(String pattern, Locale locale) {
            this.mFormatter = DateTimeFormat.forPattern(pattern).withLocale(
                    locale);
            this.mDefaultFormatter = DateTimeFormat.forPattern(DEFAULT_FORMAT)
                    .withLocale(locale);
        }

        @Override
        public StringBuffer format(Date date, StringBuffer stringbuffer,
                FieldPosition fieldposition) {
            if (date != null) {
                return stringbuffer.append(mFormatter.print(date.getTime()));
            }
            return null;
        }

        @Override
        public Date parse(String s, ParsePosition parseposition) {
            DateTime dateTime;
            try {
                dateTime = mFormatter.parseDateTime(s.substring(parseposition
                        .getIndex()));
            } catch (Exception ex) {
                dateTime = mDefaultFormatter.parseDateTime(s
                        .substring(parseposition.getIndex()));
            }
            Date date = new Date(dateTime.getMillis());
            parseposition.setIndex(parseposition.getIndex() + 25);
            return date;
        }
    }

    public static int compare(Date a, Date b) {
        long diff = a.getTime() - b.getTime();

        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }
}
