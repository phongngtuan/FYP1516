package com.ntu.phongnt.healthdroid.graph.util.formatter;

import org.junit.Test;

import java.text.DateFormat;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

public class DataEntryByMonthFormatterTest {
    @Test
    public void testDateRangeByDay() {
        DataEntryByMonthFormatter formatter = new DataEntryByMonthFormatter(null, null);
        DateFormat dateFormat = formatter.getDateFormat();
        String firstDate = "01/2015";
        String lastDate = "03/2015";
        DataEntryByMonthFormatter.DateRangeByMonth dateRangeByMonth
                = new DataEntryByMonthFormatter.DateRangeByMonth(firstDate, lastDate);
        GregorianCalendar first = new GregorianCalendar();
        first.setTime(dateRangeByMonth.getFirst().getTime());
        assertEquals(firstDate, dateFormat.format(first.getTime()));

        GregorianCalendar last = new GregorianCalendar();
        last.setTime(dateRangeByMonth.getLast().getTime());
        assertEquals("11/2015", dateFormat.format(last.getTime()));
    }
}