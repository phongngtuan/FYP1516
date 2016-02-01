package com.ntu.phongnt.healthdroid.data;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import com.ntu.phongnt.healthdroid.db.HealthDroidDatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoadDataCursorTaskTest {
    HealthDroidDatabaseHelper db;

    @Before
    public void setUp() throws Exception {
        RenamingDelegatingContext renamingDelegatingContext
                = new RenamingDelegatingContext(InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext(), "test_");

        db = new HealthDroidDatabaseHelper(renamingDelegatingContext);
    }

    @Test
    public void testDoQuery() throws Exception {
        new LoadDataCursorTask<Void>(db).doQuery();
    }
}