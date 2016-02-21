package com.ntu.phongnt.healthdroid.graph.util;

import java.util.GregorianCalendar;

public abstract class DateRange {
    GregorianCalendar first;
    GregorianCalendar last;

    public abstract int getTimeUnit();

    @SuppressWarnings("ResourceType")
    public void normalize() {
        GregorianCalendar temp = new GregorianCalendar();
        temp.setTime(first.getTime());
        temp.add(getTimeUnit(), 10);
        if (temp.after(last)) {
            last.setTime(temp.getTime());
        }
    }

    public GregorianCalendar getFirst() {
        return first;
    }

    public void setFirst(GregorianCalendar first) {
        this.first = first;
    }

    public GregorianCalendar getLast() {
        return last;
    }

    public void setLast(GregorianCalendar last) {
        this.last = last;
    }
}
