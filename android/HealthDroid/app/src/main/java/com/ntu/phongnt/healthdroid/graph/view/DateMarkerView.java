package com.ntu.phongnt.healthdroid.graph.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.ntu.phongnt.healthdroid.R;

import java.util.List;

public class DateMarkerView extends MarkerView {

    private TextView dateView;
    private List<String> xVals;

    public DateMarkerView(Context context, int layoutResource, List<String> xVals) {
        super(context, layoutResource);
        this.xVals = xVals;
        this.dateView = (TextView) findViewById(R.id.dateView);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        dateView.setText(xVals.get(highlight.getXIndex()));
    }

    @Override
    public int getXOffset(float xpos) {
        return 0;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }
}
