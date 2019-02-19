package com.romanport.arkwebmap;

public class TimeTool {
    public double totalMilliseconds;
    public double totalSeconds;
    public double totalMinutes;
    public double totalHours;
    public double totalDays;

    public double milliseconds;
    public double seconds;
    public double minutes;
    public double hours;
    public double days;

    public TimeTool(double ms) {
        double totalMs = ms;

        days = Math.floor(ms / (1000 * 60 * 60 * 24));
        ms -=  days * (1000 * 60 * 60 * 24);

        hours = Math.floor(ms / (1000 * 60 * 60));
        ms -= hours * (1000 * 60 * 60);

        minutes = Math.floor(ms / (1000 * 60));
        ms -= minutes * (1000 * 60);

        seconds = Math.floor(ms / (1000));
        ms -= seconds * (1000);
        milliseconds = ms;

        totalMilliseconds = totalMs;
        totalSeconds = seconds + (minutes * 60) + (hours * 60 * 60) + (days * 60 * 60 * 24);
        totalMinutes = (minutes) + (hours * 60) + (days * 60 * 24);
        totalHours = (hours) + (days * 24);
        totalDays = days;
    }

    public String ToTimeString() {
        //TODO: Handle negative values

        //Do conversions
        String baseString = padNum(hours)+":"+padNum(minutes)+":"+padNum(seconds);
        if(days != 0) {
            baseString = padNum(days)+":"+baseString;
        }
        return baseString;
    }

    public String padNum(double d) {
        return Integer.toString((int)Math.floor(d));//.padStart(2, "0");
    }
}
