package fr.lanfix.itemshuffle.utils;

public class TimeUtils {

    public static String getTimeString(int minutes, int seconds, int ticks) {
        return (minutes == 0 ? "" : minutes + ":")
                + (seconds < 10 ? "0": "") + (seconds + ((double) ticks) / 20)
                + (minutes == 0 ? " seconds." : ".");
    }

    public static String getTimeString(int ticks) {
        int seconds = ticks / 20;
        ticks %= 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return getTimeString(minutes, seconds, ticks);
    }

}
