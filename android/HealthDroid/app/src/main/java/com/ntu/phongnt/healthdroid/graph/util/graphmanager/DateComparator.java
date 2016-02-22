package com.ntu.phongnt.healthdroid.graph.util.graphmanager;

import java.util.Comparator;

public class DateComparator implements Comparator<String> {
    @Override
    public int compare(String lhs, String rhs) {
        String[] lhsKeys = lhs.split("/");
        String[] rhsKeys = rhs.split("/");

        for (int i = lhsKeys.length - 1; i >= 0; i--) {
            int lhsValue = Integer.parseInt(lhsKeys[i]);
            int rhsValue = Integer.parseInt(rhsKeys[i]);
            if (lhsValue != rhsValue)
                return lhsValue - rhsValue;
        }
        return 0;
    }
}
