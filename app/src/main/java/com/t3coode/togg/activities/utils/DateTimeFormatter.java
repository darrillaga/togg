package com.t3coode.togg.activities.utils;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTimeComparator;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.t3coode.togg.R;
import com.t3coode.togg.ToggApp;

public class DateTimeFormatter {

    public static String periodHourBased(long secDuration) {
        PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
        PeriodFormatter formatter = null;

        String secondsAbbr = ToggApp.getApplication().getString(
                R.string.seconds_abbr);
        String minutesAbbr = ToggApp.getApplication().getString(
                R.string.minutes_abbr);
        String hoursAbbr = ToggApp.getApplication().getString(
                R.string.hours_abbr);

        if (secDuration < 60) {
            formatter = builder.appendSeconds().appendSuffix(" " + secondsAbbr)
                    .toFormatter();

        } else if (secDuration < 3600) {
            formatter = builder.printZeroAlways().minimumPrintedDigits(2)
                    .appendMinutes().appendSeparator(":").appendSeconds()
                    .appendLiteral(" " + minutesAbbr).toFormatter();
        } else {
            formatter = builder.printZeroAlways().minimumPrintedDigits(2)
                    .appendHours().appendSeparator(":").appendMinutes()
                    .appendSeparator(":").appendSeconds().toFormatter();
        }

        Period period = new Period(secDuration * 1000);
        period = period.normalizedStandard(PeriodType.time());

        return formatter.print(period);
    }

    public static String periodHourMinBased(long secDuration) {
        PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
        PeriodFormatter formatter = null;
        String minutesAbbr = ToggApp.getApplication().getString(
                R.string.minutes_abbr);
        ;
        String hoursAbbr = ToggApp.getApplication().getString(
                R.string.hours_abbr);
        ;

        formatter = builder.printZeroAlways().minimumPrintedDigits(1)
                .appendHours().appendLiteral(" " + hoursAbbr + " ")
                .minimumPrintedDigits(2).appendMinutes()
                .appendLiteral(" " + minutesAbbr).toFormatter();

        Period period = new Period(secDuration * 1000);

        return formatter.print(period.normalizedStandard());
    }

    public static String hhMMAMPM(Date date) {
        org.joda.time.format.DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendClockhourOfHalfday(1).appendLiteral(":")
                .appendMinuteOfHour(2).appendLiteral(" ")
                .appendHalfdayOfDayText().toFormatter();

        if (date != null) {
            return formatter.print(date.getTime());
        } else {
            return "";
        }
    }

    public static String onlyDate(Date date) {
        org.joda.time.format.DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendDayOfMonth(2).appendLiteral("/").appendMonthOfYear(2)
                .appendLiteral("/").appendYear(2, 4).toFormatter();

        return formatter.print(date.getTime());
    }

    public static String literalDate(Date date) {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .appendDayOfWeekShortText().appendLiteral(" ")
                .appendDayOfMonth(1).appendLiteral(". ")
                .appendMonthOfYearShortText();

        if (date.getYear() != new Date().getYear()) {
            builder.appendLiteral(" ").appendYear(2, 4);
        }

        org.joda.time.format.DateTimeFormatter formatter = builder
                .toFormatter();

        return formatter.print(date.getTime());
    }

    public static String hhMM(Date date) {
        org.joda.time.format.DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendClockhourOfHalfday(2).appendLiteral(":")
                .appendMinuteOfHour(2).appendLiteral(" ").toFormatter();

        return formatter.print(date.getTime());
    }

    public static String dayOrDate(Date date) {
        int actualDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int actualYear = Calendar.getInstance().get(Calendar.YEAR);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        DateTimeComparator comparator = DateTimeComparator
                .getDateOnlyInstance();

        String result;
        if (comparator.compare(date, new Date()) == 0) {
            result = ToggApp.getApplication().getResources()
                    .getString(R.string.today);
        } else if (actualYear == c.get(Calendar.YEAR)
                && actualDayOfYear - c.get(Calendar.DAY_OF_YEAR) == 1) {
            result = ToggApp.getApplication().getResources()
                    .getString(R.string.yesterday);
        } else {
            result = literalDate(date);
        }

        return result;
    }
}
