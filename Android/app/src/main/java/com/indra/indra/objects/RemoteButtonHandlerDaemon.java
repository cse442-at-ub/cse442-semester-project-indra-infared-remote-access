package com.indra.indra.objects;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.nkzawa.socketio.client.Socket;
import com.indra.indra.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RemoteButtonHandlerDaemon extends Thread {

    public static final String REMOTE_NAME_KEY = "REMOTE_NAME";
    public static final String BUTTON_NAME_KEY = "BUTTON_NAME";
    public static final String BUTTON_EVENT_KEY = "BUTTON_EVENT";
    public static final int BUTTON_EVENT_DOWN = 0;
    public static final int BUTTON_EVENT_UP = 1;
    public static final long WAIT_DURATION = 500;

    private static RemoteButtonHandlerDaemon instance;

    private String remoteName;
    private String buttonName;
    private Handler buttonEventMessageHandler;
    private long timestamp;
    private boolean running;
    private Socket clientSocket;
    private MainActivity mainActivity;

    private ReentrantReadWriteLock lock;



    @SuppressLint("HandlerLeak")
    private RemoteButtonHandlerDaemon(Socket clientSocket, final MainActivity activity){
        super();

        this.buttonEventMessageHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Bundle eventBundle = msg.getData();
                String rName = eventBundle.getString(REMOTE_NAME_KEY);
                String bName = eventBundle.getString(BUTTON_NAME_KEY);
                int event = eventBundle.getInt(BUTTON_EVENT_KEY);

                if(event == BUTTON_EVENT_DOWN){
                    if(remoteName == null && buttonName == null){
                        lock.writeLock().lock();
                        remoteName = rName;
                        buttonName = bName;
                        lock.writeLock().unlock();
                        pressButton("ONCE", rName, bName, true);
                        timestamp = System.currentTimeMillis();
                       // Press button
                    }
                } else if(event == BUTTON_EVENT_UP){
                    if(remoteName != null && buttonName != null && remoteName.equals(rName)
                            && buttonName.equals(bName)){
                        lock.writeLock().lock();
                        remoteName = null;
                        buttonName = null;
                        if(timestamp == -1){
                            pressButton("STOP", rName, bName, false);
                        }
                        timestamp = -1;
                        mainActivity.unlockNavigationDrawer();
                        lock.writeLock().unlock();
                    }
                }
            }
        };

        this.clientSocket = clientSocket;
        this.mainActivity = activity;
        this.lock = new ReentrantReadWriteLock();
        this.setDaemon(true);
        this.timestamp = -1;
    }

    public static RemoteButtonHandlerDaemon getInstance(Socket clientSocket, MainActivity activity){
        if(instance == null){
            instance = new RemoteButtonHandlerDaemon(clientSocket, activity);
            instance.start();
        }

        return instance;
    }

    @Override
    public synchronized void start() {
        this.running = true;
        super.start();
    }

    @Override
    public void run() {

        while(running){
            lock.readLock().lock();
            if(timestamp != -1 && System.currentTimeMillis() - timestamp >= WAIT_DURATION){
                pressButton("START", remoteName, buttonName, true);
                mainActivity.lockNavigationDrawer();
                timestamp = -1;
            }
            lock.readLock().unlock();
        }
    }


    private void pressButton(String method, String remoteName, String buttonName, boolean vibrate){
        HashMap<String, String> jsonMap = new HashMap<>();
        jsonMap.put("remote", remoteName);
        jsonMap.put("button", buttonName);
        jsonMap.put("method", method);
        jsonMap.put("ipAddress", mainActivity.getRaspberryPiIP());
        JSONObject message = new JSONObject(jsonMap);
        clientSocket.emit("button_press", message.toString());
        Log.d("SOCKETIO", message.toString());
        if(vibrate){
            vibrateOnClick();
        }
    }

    private void vibrateOnClick(){
        Vibrator vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(20, 255));
    }



    public Handler getButtonEventMessageHandler() {
        return buttonEventMessageHandler;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
