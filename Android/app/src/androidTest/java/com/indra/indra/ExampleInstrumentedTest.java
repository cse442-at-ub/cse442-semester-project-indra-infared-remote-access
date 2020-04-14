package com.indra.indra;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.indra.indra.db.DatabaseUtil;
import com.indra.indra.models.RemoteButtonModel;
import com.indra.indra.models.RemoteModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    public RemoteModel testDevice;
    public RemoteModel testDevice2;
    public DatabaseUtil util;

    @Before
    public void setUpDataBase(){
        testDevice = new RemoteModel("DISPLAY NAME", "LIRC NAME");
        testDevice2 = new RemoteModel("NAME 2", "LIRC 2");
        util = new DatabaseUtil(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.indra.indra", appContext.getPackageName());
    }

    @Test
    public void testDBContainsDevice(){
        util.insertDeviceToDatabase(testDevice);
        util.insertDeviceToDatabase(testDevice2);
        ArrayList<RemoteModel> devices = util.getDevicesForUser(DatabaseUtil.DEFAULT_USER);

        for(int i = 0; i < devices.size(); i++){
            Log.i("TEST MSG", devices.get(i).getDeviceId() + " " + devices.get(i).getDisplayName());
        }

        Assert.assertEquals(2, devices.size());

        RemoteModel retrievedDevice = devices.get(0);

        Assert.assertEquals(testDevice.getDeviceId(), retrievedDevice.getDeviceId());
        Assert.assertEquals(testDevice.getButtonModels(), retrievedDevice.getButtonModels());
        Assert.assertEquals(testDevice.getLircName(), retrievedDevice.getLircName());
        Assert.assertEquals(testDevice.getDisplayName(), retrievedDevice.getDisplayName());

//        util.dropAllTables();
        util.resetTables();
        devices = util.getDevicesForUser(DatabaseUtil.DEFAULT_USER);
        Assert.assertEquals(0, devices.size());
    }

    @Test
    public void testInsertingRemoteWithButtons(){
        RemoteModel model = new RemoteModel("TEST", "TEST", DatabaseUtil.DEFAULT_USER, -1);
        model.addButtonModel(new RemoteButtonModel("LIRC", "LIRC", -1, -1));

        util.resetTables();
        util.insertDeviceToDatabase(model);

        RemoteModel retrieved = util.getDevicesForUser(DatabaseUtil.DEFAULT_USER).get(0);
        Assert.assertEquals(1, retrieved.getButtonModels().size());

        util.resetTables();
    }

//    @After
//    public void destroyDB(){
//        util.dropAllTables();
//    }
}
