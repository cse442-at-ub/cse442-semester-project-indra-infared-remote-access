package com.indra.indra.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.indra.indra.models.RemoteModel;
import com.indra.indra.models.RemoteButtonModel;

import java.util.ArrayList;


public class DatabaseUtil extends SQLiteOpenHelper {

    public static final String DB_NAME = "Indra_DB";
    public static final int DB_VERSION = 1;
    public static final String DEFAULT_USER = "DEFAULT";

    public static final String REMOTE_TABLE_NAME = "remoteTable";
    public static final String REMOTE_COLUMN_ID = "id";
    public static final String REMOTE_COLUMN_LIRC_NAME = "lircName";
    public static final String REMOTE_COLUMN_DISPLAY_NAME = "displayName";
    public static final String REMOTE_COLUMN_USER = "user";

    public static final String BUTTON_TABLE_NAME = "buttonTable";
    public static final String BUTTON_COLUMN_ID = "id";
    public static final String BUTTON_COLUMN_LIRC_NAME = "lircName";
    public static final String BUTTON_COLUMN_DISPLAY_NAME = "displayName";
    public static final String BUTTON_COLUMN_REMOTE_ID = "remoteId";


    public DatabaseUtil(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        setUpTables(this.getWritableDatabase());
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        setUpTables(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    public ArrayList<RemoteModel> getDevicesForUser(String user){
        ArrayList<RemoteModel> usersDevices = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String whereClause = REMOTE_COLUMN_USER + "= ?";
        String[] whereArgs = new String[]{user};
        Cursor cursor = db.query(REMOTE_TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            String lircName = cursor.getString(cursor.getColumnIndex(REMOTE_COLUMN_LIRC_NAME));
            String displayName = cursor.getString(cursor.getColumnIndex(REMOTE_COLUMN_DISPLAY_NAME));
            int deviceId = cursor.getInt(cursor.getColumnIndex(REMOTE_COLUMN_ID));
            RemoteModel device = new RemoteModel(displayName, lircName, user, deviceId);
            device.setButtonModels(getButtonsForRemoteWithId(deviceId));
            usersDevices.add(device);
            cursor.moveToNext();
        }

        cursor.close();
        
        return usersDevices;
    }


    public ArrayList<RemoteButtonModel> getButtonsForRemoteWithId(long remoteId){
        ArrayList<RemoteButtonModel> buttonModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        

        String whereClause = BUTTON_COLUMN_REMOTE_ID + "=?";
        String[] whereArgs = new String[] {Long.toString(remoteId)};
        Cursor cursor = db.query(BUTTON_TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            int buttonId = cursor.getInt(cursor.getColumnIndex(BUTTON_COLUMN_ID));
            String displayName = cursor.getString(cursor.getColumnIndex(BUTTON_COLUMN_DISPLAY_NAME));
            String lircName = cursor.getString(cursor.getColumnIndex(BUTTON_COLUMN_LIRC_NAME));

            RemoteButtonModel model = new RemoteButtonModel(displayName, lircName, remoteId, buttonId);
            buttonModels.add(model);
            cursor.moveToNext();
        }

        cursor.close();

        
        return buttonModels;
    }


    public boolean insertDeviceToDatabase(RemoteModel device){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(REMOTE_COLUMN_DISPLAY_NAME, device.getDisplayName());
        contentValues.put(REMOTE_COLUMN_LIRC_NAME, device.getLircName());
        contentValues.put(REMOTE_COLUMN_USER, device.getUser());

        long id = db.insert(REMOTE_TABLE_NAME, null, contentValues);
        if (id == -1){
            return false;
        }

        device.setDeviceId(id);

        for(RemoteButtonModel buttonModel : device.getButtonModels()){
            buttonModel.setRemoteId(id);
        }

        if(!insertButtons(device.getButtonModels())){
            String deleteWhere = REMOTE_COLUMN_ID + "=?";
            String[] whereArgs = new String[]{Long.toString(device.getDeviceId())};

            db.delete(REMOTE_TABLE_NAME, deleteWhere, whereArgs);

            return false;
        }

        
        return true;
    }


    private boolean insertButtons(ArrayList<RemoteButtonModel> buttonModels){
        SQLiteDatabase db = this.getWritableDatabase();

        for(RemoteButtonModel model : buttonModels){
            ContentValues values = new ContentValues();
            values.put(BUTTON_COLUMN_DISPLAY_NAME, model.getDisplayName());
            values.put(BUTTON_COLUMN_LIRC_NAME, model.getLircName());
            values.put(BUTTON_COLUMN_REMOTE_ID, model.getRemoteId());
            long buttonId = db.insert(BUTTON_TABLE_NAME, null, values);
            if (buttonId == -1){
                return false;
            }
            model.setId(buttonId);
        }

        
        return true;
    }


    public void setUpTables(SQLiteDatabase db ){

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + REMOTE_TABLE_NAME +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, lircName TEXT, displayName TEXT, user TEXT);"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + BUTTON_TABLE_NAME +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, lircName TEXT, displayName TEXT, remoteId INTEGER);"
        );

        
    }

    public void dropAllTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + REMOTE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE " + BUTTON_TABLE_NAME + ";");
    }


    public void resetTables(){
        dropAllTables();
        setUpTables(this.getWritableDatabase());
    }


    public boolean deleteRemote(RemoteModel model){
        SQLiteDatabase db = this.getWritableDatabase();
        String remotesWhereClause = REMOTE_COLUMN_ID + "=?";
        String buttonsWhereClause = BUTTON_COLUMN_REMOTE_ID + "=?";
        String[] whereArgs = new String[]{Long.toString(model.getDeviceId())};

        int total = db.delete(REMOTE_TABLE_NAME, remotesWhereClause, whereArgs);
        total += db.delete(BUTTON_TABLE_NAME, buttonsWhereClause, whereArgs);

        return total == model.getButtonModels().size() + 1;
    }
}
