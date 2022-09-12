package me.congresse.ticketsvalidation.utils;

import android.text.Html;

public class Utils {

    public static String html2text(String html) {
        return Html.fromHtml(html).toString();
    }
}
