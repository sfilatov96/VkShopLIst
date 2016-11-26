package com.vkshoplist.sfilatov96.vkshoplist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VkMessangerService extends Service {
    //AsyncHttpClient client = new AsyncHttpClient();
    private Thread thread;
    private int PAUSE = 5000;
    private static final int NOTIFY_ID = 101;
    private String key;
    private String server;
    private String ts;
    private final int MESSAGE = 6;
    private final int FRIEND_ID = 3;
    private JSONObject js;
    OkHttpClient client = new OkHttpClient();
    VkHelper vkHelper = new VkHelper(this);



    public VkMessangerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("service","bind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        key = intent.getStringExtra("KEY");
        server = intent.getStringExtra("SERVER");
        ts = intent.getStringExtra("TS");
        //getJsonFromVk();
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        try {

                            Thread.sleep(PAUSE);
                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.d("service",key+ ' ' + server + ' ' + ts);
                        getJsonFromVk();
                        if (js != null) {
                            try {
                                findShopListInJsonArray(js.getJSONArray("updates"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //runNotification();

                    }

                }
            });
            thread.start();
        }

        return Service.START_STICKY;
    }
    private void findShopListInJsonArray(JSONArray jsonArray){
        Log.d("jsonarray",jsonArray.toString());
        if(jsonArray.toString().contains("VkShopList_Open")) {
            refreshLongPollServer();
            ArrayList<JSONArray> list = new ArrayList<JSONArray>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    list.add(jsonArray.getJSONArray(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            workWithList(list);
        }
    }
    private void workWithList(ArrayList<JSONArray> list){
        for(JSONArray Ja: list){
            if(Ja.toString().contains("VkShopList_Open")){
                try {
                    String ShopList = Ja.getString(MESSAGE).replace("&quot;","\"");
                    final JSONObject jsonShopList = new JSONObject(ShopList);
                    String friend = Ja.getString(FRIEND_ID);
                    vkHelper.getProfileById(friend);
                    vkHelper.setListener(new VkHelper.Listener() {
                        @Override
                        public void onAppearFriends(ArrayList<Person> persons) {

                        }

                        @Override
                        public void onAppearUserProfile(JSONObject jsonObject) {
                            Log.d("user_appear",jsonObject.toString());
                            try {

                                saveInDataBase(jsonShopList,jsonObject.getString("first_name"),jsonObject.getString("last_name"),jsonObject.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    private void getJsonFromVk(){
        String url = "https://"+server+"?act=a_check&key="+key+"&ts="+ts+"&wait=25&mode=2&version=1";
        //Log.d("url",url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            try {
                js =  new JSONObject(response.body().string());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public void runNotification(String firstname, String lastname){
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setAutoCancel(true)
                .setContentTitle("VkShopList")
                .setContentText(String.format("%s %s отправил(а) вам список!",firstname,lastname));

        Notification notification = builder.build();

        notification.defaults = Notification.DEFAULT_ALL;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, notification);
    }

    private void refreshLongPollServer(){
        vkHelper.getLongPolling();
        vkHelper.setLongPollListener(new VkHelper.LongPollListener() {
            @Override
            public void onGetLongPoll(String key, String server, String ts) {
                VkMessangerService.this.key = key;
                VkMessangerService.this.server = server;
                VkMessangerService.this.ts = ts;
            }
        });
    }




    private void saveInDataBase(JSONObject jsonObject, String firstname, String lastname, String user_id){
        Date date = new Date();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        TableShopListClass tableShopListClass;
        try {
            String strArr = jsonObject.getString("VkShopList_Open");
            JSONArray jsonArray = new JSONArray(strArr);
            JSONObject jsobj;
            jsobj = jsonArray.getJSONObject(0);
            List<TableShopListAuthor> ta = TableShopListAuthor.find(TableShopListAuthor.class,"title = ? and user_id",jsobj.getString("list_title"),user_id);
            if(ta.isEmpty()) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsobj = jsonArray.getJSONObject(i);
                    ShopListItem shopListItem = new ShopListItem(jsobj.getString("name"), jsobj.getString("quantity"),
                            jsobj.getString("value"), jsobj.getString("list_title"));

                    Log.d("shoplistitem", shopListItem.listTitle + ' ' + shopListItem.name + ' ' + shopListItem.value + ' ' + shopListItem.quantity);
                    Log.d("shoplistitem", jsonArray.getJSONObject(0).getString("list_title"));
                    tableShopListClass = new TableShopListClass(shopListItem);
                    tableShopListClass.save();
                }


                TableShopListAuthor tableShopListAuthor = new TableShopListAuthor(firstname + ' ' + lastname, jsobj.getString("list_title"), true, false, user_id);
                tableShopListAuthor.save();

                runNotification(firstname, lastname);
            }



        } catch (JSONException e) {
            Log.d("db","except");
            e.printStackTrace();
        }

    }




}
