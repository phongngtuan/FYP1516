package com.ntu.phongnt.healthdroid.graph.util.formatter;

import org.junit.Test;

import java.text.DateFormat;
import java.util.GregorianCalendar;

public class DataEntryByWeekFormatterTest {
    @Test
    public void testDateRangeByWeek() {
        DataEntryByWeekFormatter formatter = new DataEntryByWeekFormatter(null, null);
        DateFormat dateFormat = formatter.getDateFormat();
        String firstDate = "01/2015";
        String lastDate = "03/2015";
        DataEntryByWeekFormatter.DateRangeByWeek dateRangeByWeek
                = new DataEntryByWeekFormatter.DateRangeByWeek(firstDate, lastDate);
        GregorianCalendar first = new GregorianCalendar();
        first.setTime(dateRangeByWeek.getFirst().getTime());
//        assertEquals(firstDate, dateFormat.format(first.getTime()));

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(dateRangeByWeek.getLast().getTime());
//        assertEquals("11/2015", dateFormat.format(last.getTime()));
    }
}