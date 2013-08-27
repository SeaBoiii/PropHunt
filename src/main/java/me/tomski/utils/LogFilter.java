package me.tomski.utils;

import java.util.logging.Filter;
import java.util.logging.LogRecord;


public class LogFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord arg0) {
        return !arg0.getMessage().contains("was kicked for floating too long!");
    }

}