package com.vkshoplist.sfilatov96.vkshoplist;


import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateListActivity extends AppCompatActivity {
    String userId;
    public final String VK_MESSAGE_IDENTIFIER="VkShopList_Open";
    public ArrayList<ShopListItem> ShopList;
    private ShopListItemRecyclerViewAdapter adapter;
    private String shopListTitle;
    String userName;
    String userAvater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
        ItemTouchHelper itemTouchHelper;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list_activty);
        setTitle(null);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        ShopList = new ArrayList<>();
        getUserFromMainActivity(intent);







        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        recyclerView = (RecyclerView) findViewById(R.id.shop_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.myFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });

        adapter = new ShopListItemRecyclerViewAdapter(this, ShopList);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    }
                })
        );


        ImageButton sendButton = (ImageButton) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendShopListToFriend(v);
            }
        });
        if(savedInstanceState != null) {
            shopListTitle = savedInstanceState.getString("shopListTitle");

        }
        if(shopListTitle != null) {

            ((TextView) findViewById(R.id.listTitle)).setText(shopListTitle);
            getCurrentShopList();

        } else {
            showTitleListDialog();
        }





    }

    void getCurrentShopList() {
        if (shopListTitle != null){
            List<TableShopListClass> allItem = TableShopListClass.find(TableShopListClass.class, "list_title = ?", shopListTitle);
            if (allItem != null) {
                for (TableShopListClass i : allItem) {
                    ShopList.add(new ShopListItem(i.name, i.quantity, i.value, i.listTitle));
                }
            }
        }

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("shopListTitle", shopListTitle);
        saveListToDataBase(true);

        super.onSaveInstanceState(savedInstanceState);
    }

    boolean saveListToDataBase(boolean is_blank) {
        if(!ShopList.isEmpty()) {
            for (ShopListItem s : ShopList) {
                List<TableShopListClass> item = TableShopListClass.find(TableShopListClass.class, "list_title = ? and name = ?", s.listTitle, s.name);
                if (item.isEmpty()) {
                    TableShopListClass tableShopListClass = new TableShopListClass(s);
                    tableShopListClass.save();
                }

            }
            List<TableShopListAuthor> is_exist = TableShopListAuthor.find(TableShopListAuthor.class,"title = ?",shopListTitle);
            if(is_exist.isEmpty()) {
                TableShopListAuthor tableShopListAuthor = new TableShopListAuthor(userName, ShopList.get(0).listTitle, false, is_blank, userId);
                tableShopListAuthor.save();
            } else {
                is_exist.get(0).is_blank = is_blank;
                is_exist.get(0).save();
            }
            return true;
        } else {
            Toast.makeText(this,R.string.list_empty,Toast.LENGTH_LONG).show();
            return false;
        }


    }


    public void getUserFromMainActivity(Intent intent){

        userId = intent.getStringExtra("id");
        VkHelper vkHelper = new VkHelper(this);
        vkHelper.getProfileById(userId);
        vkHelper.setListener(new VkHelper.Listener() {
            @Override
            public void onAppearFriends(ArrayList<Person> persons) {

            }

            @Override
            public void onAppearUserProfile(JSONObject jsonObject) {

                try {
                    userName = jsonObject.getString("first_name")+' '+jsonObject.getString("last_name");
                    userAvater = jsonObject.getString("photo_200");
                    ImageView toolbarPhoto = (ImageView) findViewById(R.id.friends_avatar);
                    Picasso.with(CreateListActivity.this)
                            .load(userAvater)
                            .transform(new CircularTransformation(80))
                            .into(toolbarPhoto);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        if(intent.getStringExtra("shopListTitle") != null) {
            shopListTitle = intent.getStringExtra("shopListTitle");
            //getCurrentShopList();
        }

    }

    public void emptyFields() {
        Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_LONG).show();
    }

    public void showTitleListDialog() {
        DialogFragment newFragment = new TitleShopListDialog();
        newFragment.show(getFragmentManager(), "other");
        newFragment.setCancelable(false);
        newFragment.setShowsDialog(true);
    }

    public void showAddItemDialog() {
        DialogFragment newFragment = new AddItemDialog();
        newFragment.show(getFragmentManager(), "dialog");

        newFragment.setCancelable(false);
        newFragment.setShowsDialog(true);
    }

    public void GetShopListTitle(String title) {
        shopListTitle = title+" - ("+getCurrentDate()+")";
        ((TextView)findViewById(R.id.listTitle)).setText(shopListTitle);
        getCurrentShopList();
    }

    public void FillShopList(String name, String quantity, String value) {

        ShopList.add(new ShopListItem(name, quantity, value, shopListTitle));
        adapter.notifyDataSetChanged();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(saveListToDataBase(true)) {
            Toast.makeText(this, R.string.saved_as_blank, Toast.LENGTH_LONG).show();
        }
        this.finish();
    }
    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT ){

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {


            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();


            List<TableShopListClass> item = TableShopListClass.find(TableShopListClass.class, "name = ? and list_title = ?", ShopList.get(position).name, ShopList.get(position).listTitle);
            if(!item.isEmpty()) {
                item.get(0).delete();
            }
            ShopList.remove(position);

            adapter.notifyDataSetChanged();
        }
    };

    void sendShopListToFriend(View view){
        if(ShopList.isEmpty()){
            Snackbar.make(view,R.string.list_empty,Snackbar.LENGTH_LONG).show();
        } else {
            ArrayList<JSONObject> jsonlist = new ArrayList<>();
            for(ShopListItem s:ShopList){
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("name",s.name);
                    jsonObject.put("quantity",s.quantity);
                    jsonObject.put("value",s.value);
                    jsonObject.put("list_title",s.listTitle);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonlist.add(jsonObject);
            }
            JSONObject prepareJson = new JSONObject();
            try {
                prepareJson.put(VK_MESSAGE_IDENTIFIER,jsonlist);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            VkHelper vkHelper = new VkHelper(this);
            vkHelper.sendShopListByFriendsID(userId,prepareJson);
            vkHelper.setMessageSendListener(new VkHelper.MessageSendListener() {
                @Override
                public void onComplete() {
                    saveListToDataBase(false);
                    CreateListActivity.this.finish();
                    Toast.makeText(CreateListActivity.this, R.string.send_success, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError() {
                    saveListToDataBase(true);
                    CreateListActivity.this.finish();
                    Toast.makeText(CreateListActivity.this, R.string.internet_access_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String getCurrentDate(){
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("d MMM yyyy H:mm:ss");
        return ft.format(dNow);
    }

}