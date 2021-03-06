package com.vkshoplist.sfilatov96.vkshoplist;



import android.app.DialogFragment;
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
    boolean is_inbox;
    boolean is_secret;
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
        if (is_secret){
            showSecretKeyDialog();
        } else {
            continueCreating();
        }



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

            is_inbox = list.get(0).is_inbox_shoplist;
            is_secret = list.get(0).is_secret;
        }

    }
    private void sendCompletion(){

        if(is_inbox) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("VkShopList_Completed", shopListTitle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            vkHelper.sendShopListByFriendsID(user_id, jsonObject);
            vkHelper.setMessageSendListener(new VkHelper.MessageSendListener() {
                @Override
                public void onComplete() {
                    List<TableShopListAuthor> list = TableShopListAuthor.find(TableShopListAuthor.class, "title = ?", shopListTitle);
                    if (list != null) {
                        list.get(0).is_performed = true;
                        list.get(0).save();
                    }
                    Toast.makeText(ExecuteListActivity.this, R.string.completed, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError() {
                    Toast.makeText(ExecuteListActivity.this, R.string.internet_access_error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this,R.string.is_outbox_shoplist,Toast.LENGTH_SHORT).show();
        }

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


            List<TableShopListClass> item = TableShopListClass.find(TableShopListClass.class, "name = ? and list_title = ?", ShopList.get(position).name, ShopList.get(position).listTitle);
            if(!item.isEmpty()) {
                item.get(0).delete();
            }
            ShopList.remove(position);

            adapter.notifyDataSetChanged();
        }
    };

    public void showSecretKeyDialog() {
        DialogFragment newFragment = new SecretKeyDialog();
        newFragment.show(getFragmentManager(), "secret");
        newFragment.setCancelable(false);
        newFragment.setShowsDialog(true);
    }

    public void continueCreating(){
        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        recyclerView = (RecyclerView) findViewById(R.id.shop_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


        adapter = new ShopListItemRecyclerViewAdapter(this, ShopList);
        recyclerView.setAdapter(adapter);
        if(is_inbox) {
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        if(is_inbox) {
            ImageButton sendButton = (ImageButton) findViewById(R.id.btn_send);
            Picasso.with(this).load(R.mipmap.ic_done_white_24dp).into(sendButton);


            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_performed) {
                        Toast.makeText(ExecuteListActivity.this, R.string.shoplist_allready_performed, Toast.LENGTH_LONG).show();
                    } else {
                        sendCompletion();
                        finish();

                    }
                }
            });
        } else {
            ImageButton sendButton = (ImageButton) findViewById(R.id.btn_send);
            sendButton.setAlpha(0);
            sendButton.setClickable(false);
        }

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

    public void emptyFields() {
        Toast.makeText(this, R.string.fields_empty, Toast.LENGTH_LONG).show();
    }
    public void invalidSecretKey() {
        Toast.makeText(this, R.string.invalid_secret_key, Toast.LENGTH_LONG).show();
    }
}
