package com.ntu.phongnt.healthdroid.util;

public class DataEntryFormatter {
    public static class DateRangeByMonth {
        public int firstMonth;
        public int firstYear;
        public int lastMonth;
        public int lastYear;

        public DateRangeByMonth(String first, String last) {
            String[] firstKey = first.split("/");
            String[] lastKey = last.split("/");
            firstMonth = Integer.parseInt(firstKey[0]);
            firstYear = Integer.parseInt(firstKey[1]);
            lastMonth = Integer.parseInt(lastKey[0]);
            lastYear = Integer.parseInt(lastKey[1]);
            normalize();
        }

        public void normalize() {
            if (getRange() < 10) {
                lastMonth = lastMonth + 10;
                if (lastMonth > 12) {
                    lastYear += 1;
                    lastMonth %= 12;
                }
            }
        }

        public int getRange() {
            return (lastYear - firstYear) * 12 + lastMonth - firstMonth;
        }
    }
}
