package com.ntu.phongnt.healthdroid.graph.util.formatter;

import org.junit.Test;

import java.text.DateFormat;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class DateRangeByDayTest {
    @Test
    public void testDateRangeByDay() {
        DataEntryByDayFormatter formatter = new DataEntryByDayFormatter(null, null);
        DateFormat dateFormat = formatter.getDateFormat();
        String firstDate = "14/12/2015";
        String lastDate = "16/12/2015";
        DataEntryByDayFormatter.DateRangeByDay dateRangeByDay
                = new DataEntryByDayFormatter.DateRangeByDay(firstDate, lastDate);
        GregorianCalendar first = new GregorianCalendar();
        first.setTime(dateRangeByDay.getFirst().getTime());
        assertEquals(firstDate, dateFormat.format(first.getTime()));

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(dateRangeByDay.getLast().getTime());
        assertEquals("24/12/2015", dateFormat.format(last.getTime()));
    }
}