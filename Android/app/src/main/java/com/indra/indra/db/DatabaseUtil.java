package com.indra.indra.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.indra.indra.models.IpAddressModel;
import com.indra.indra.models.RemoteModel;
import com.indra.indra.models.RemoteButtonModel;

import java.util.ArrayList;


public class DatabaseUtil extends SQLiteOpenHelper {

    public static final String DB_NAME = "Indra_DB";
    public static final int DB_VERSION = 3;
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

    public static final String IP_TABLE_NAME = "ipTable";
    public static final String IP_COLUMN_ID = "id";
    public static final String IP_COLUMN_IP_ADDR = "ipAddress";
    public static final String IP_COLUMN_USER = "user";


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

        db.execSQL(
                String.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT);",
                        IP_TABLE_NAME, IP_COLUMN_ID, IP_COLUMN_IP_ADDR, IP_COLUMN_USER)
        );

        
    }

    public void dropAllTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE " + REMOTE_TABLE_NAME + ";");
        db.execSQL("DROP TABLE " + BUTTON_TABLE_NAME + ";");
        db.execSQL("DROP TABLE " + IP_TABLE_NAME + ";");
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


    public RemoteModel getDeviceById(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        String whereClause = REMOTE_COLUMN_ID + "=?";
        String[] whereArgs = new String[]{ Long.toString(id) };

        Cursor cursor = db.query(REMOTE_TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        if(cursor.moveToFirst()){
            String lircName = cursor.getString(cursor.getColumnIndex(REMOTE_COLUMN_LIRC_NAME));
            String displayName = cursor.getString(cursor.getColumnIndex(REMOTE_COLUMN_DISPLAY_NAME));
            String username = cursor.getString(cursor.getColumnIndex(REMOTE_COLUMN_USER));

            RemoteModel model = new RemoteModel(displayName, lircName, username, id);
            model.setButtonModels(getButtonsForRemoteWithId(id));

            return model;
        }

        return null;
    }

    public RemoteModel updateRemoteDisplayName(long id, String displayName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REMOTE_COLUMN_DISPLAY_NAME, displayName);

        String whereClause = REMOTE_COLUMN_ID + "=?";
        String[] whereArgs = new String[]{ Long.toString(id) };


        int affected = db.update(REMOTE_TABLE_NAME, values, whereClause, whereArgs);

        if(affected != 1){
            return null;
        }

        return getDeviceById(id);
    }


    public boolean updateRemoteUsernames(String oldName, String newName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REMOTE_COLUMN_USER, newName);
        String whereClause = REMOTE_COLUMN_USER + "=?";
        String[] whereArgs = new String[]{ oldName };
        int affected = db.update(REMOTE_TABLE_NAME, values, whereClause, whereArgs);

        values = new ContentValues();
        values.put(IP_COLUMN_USER, newName);
        whereClause = IP_COLUMN_USER + "=?";
        whereArgs = new String[]{ oldName };

        int affected1 = db.update(IP_TABLE_NAME, values, whereClause, whereArgs);

        return affected > 0 || affected1 > 0;
    }

    public IpAddressModel updateIpRow(String ip, String user){
        IpAddressModel ipModel = getIpForUser(user);
        SQLiteDatabase db = this.getWritableDatabase();

        if(ipModel != null){
            ContentValues values = new ContentValues();
            values.put(IP_COLUMN_IP_ADDR, ip);

            String whereClause = IP_COLUMN_ID + "=?";
            String[] whereArgs = new String[]{ Long.toString(ipModel.getId()) };


            int affected = db.update(IP_TABLE_NAME, values, whereClause, whereArgs);
            ipModel.setIpAddress(ip);
        } else {
            ContentValues values = new ContentValues();
            values.put(IP_COLUMN_USER, user);
            values.put(IP_COLUMN_IP_ADDR, ip);
            long buttonId = db.insert(IP_TABLE_NAME, null, values);
            ipModel = new IpAddressModel(buttonId, ip, user);
        }

        return ipModel;
    }


    public IpAddressModel getIpForUser(String user){
        IpAddressModel ip = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String whereClause = IP_COLUMN_USER + "=?";
        String[] whereArgs = new String[]{ user };

        Cursor cursor = db.query(IP_TABLE_NAME, null, whereClause, whereArgs, null, null, null);

        if(cursor.moveToFirst()){
            long id = cursor.getLong(cursor.getColumnIndex(IP_COLUMN_ID));
            String ipAddr = cursor.getString(cursor.getColumnIndex(IP_COLUMN_IP_ADDR));

            ip = new IpAddressModel(id, ipAddr, user);
        }

        return ip;
    }
}
