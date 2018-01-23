package com.soumya.wwdablu.barqr.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getCurrentDate() {

        return new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
    }
}
