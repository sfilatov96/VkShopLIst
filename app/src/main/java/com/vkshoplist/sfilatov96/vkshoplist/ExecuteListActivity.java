package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExecuteListActivity extends AppCompatActivity {
    String shopListTitle;
    ArrayList<ShopListItem> ShopList;
    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;
    ShopListItemRecyclerViewAdapter adapter;
    VkHelper vkHelper;
    String user_id;
    boolean is_performed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vkHelper = new VkHelper(this);
        setContentView(R.layout.activity_execute_list_actyvity);
        ShopList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            shopListTitle = intent.getStringExtra("SHOPLIST_TITLE");
        }
        if(savedInstanceState != null){
            shopListTitle = savedInstanceState.getString("SHOPLIST_TITLE");
        }
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.listTitle)).setText(shopListTitle);

        fillShopListFromDataBase();

        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        recyclerView = (RecyclerView) findViewById(R.id.shop_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


        adapter = new ShopListItemRecyclerViewAdapter(this, ShopList);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ImageButton sendButton = (ImageButton) findViewById(R.id.btn_send);
        Picasso.with(this).load(R.mipmap.ic_done_white_24dp).into(sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_performed){
                    Toast.makeText(ExecuteListActivity.this,R.string.shoplist_allready_performed,Toast.LENGTH_LONG).show();
                } else {
                    sendCompletion();
                }
            }
        });

        vkHelper.getProfileById(user_id);
        vkHelper.setListener(new VkHelper.Listener() {
            @Override
            public void onAppearFriends(ArrayList<Person> persons) {

            }

            @Override
            public void onAppearUserProfile(JSONObject jsonObject) {
                ImageView toolbarPhoto = (ImageView) findViewById(R.id.friends_avatar);
                try {
                    Picasso.with(ExecuteListActivity.this)
                            .load(jsonObject.getString("photo_200"))
                            .transform(new CircularTransformation(80))
                            .into(toolbarPhoto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    private void fillShopListFromDataBase(){
        List<TableShopListClass> allItem = TableShopListClass.find(TableShopListClass.class, "list_title = ?", shopListTitle);
        if (allItem != null) {
            for (TableShopListClass i : allItem) {
                ShopList.add(new ShopListItem(i.name, i.quantity, i.value, i.listTitle));
                Log.d("list",i.name + i.quantity + i.value + i.listTitle);
            }
        }
        List<TableShopListAuthor> list = TableShopListAuthor.find(TableShopListAuthor.class, "title = ?", shopListTitle);
        if(list != null){
            user_id = list.get(0).uservk;
            is_performed = list.get(0).is_performed;
        }

    }
    private void sendCompletion(){
        List<TableShopListAuthor> list = TableShopListAuthor.find(TableShopListAuthor.class, "title = ?", shopListTitle);
        if(list != null){
            list.get(0).is_performed = true;
            list.get(0).save();
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("VkShopList","is_completed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        vkHelper.sendShopListByFriendsID(user_id,jsonObject);
        vkHelper.setMessageSendListener(new VkHelper.MessageSendListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onError() {

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SHOPLIST_TITLE",shopListTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT ){

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {


            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Log.d("dada","remove item in " + ShopList.get(position).listTitle + " ShopList: { name: " + ShopList.get(position).name + "}" );


            List<TableShopListClass> item = TableShopListClass.find(TableShopListClass.class, "name = ? and list_title = ?", ShopList.get(position).name, ShopList.get(position).listTitle);
            if(!item.isEmpty()) {
                item.get(0).delete();
            }
            ShopList.remove(position);

            adapter.notifyDataSetChanged();
        }
    };
}
