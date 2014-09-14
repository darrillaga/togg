package com.t3coode.togg.activities.utils;

import java.text.DecimalFormat;
import java.text.ParseException;

public class DecimalFormatter {

    public static final String SINGLE_FORMAT = "0.00";

    public static String singleFormat(Double value) {
        if (value != null) {
            DecimalFormat df = new DecimalFormat(SINGLE_FORMAT);
            return df.format(value);
        } else {
            return null;
        }
    }

    public static Double singleParse(String value) throws ParseException {
        if (value != null) {
            DecimalFormat df = new DecimalFormat(SINGLE_FORMAT);
            return df.parse(value).doubleValue();
        } else {
            return null;
        }
    }

}
