package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKScope;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateListActivty extends AppCompatActivity {
    String userId;
    final String VKUSERID = "VkUserId";
    final String VK_MESSAGE_IDENTIFIER="VkShopList";
    public ArrayList<ShopListItem> ShopList;
    private ShopListRVAdapter adapter;
    private String shopListTitle;
    private Toolbar toolbar;
    private AlertDialog.Builder ad;
    private ItemTouchHelper itemTouchHelper;
    String user;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list_activty);
        setTitle(null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
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
        ShopList = new ArrayList<>();
        adapter = new ShopListRVAdapter(this, ShopList);
        recyclerView.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ImageButton sendButton = (ImageButton) findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendShopListToFriend(v);
            }
        });

        if( savedInstanceState == null ) {
            showTitleListDialog();
        } else {
            shopListTitle = savedInstanceState.getString("shopListTitle");
            ((TextView)findViewById(R.id.listTitle)).setText(shopListTitle);
            getCurrentShopList();
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
        // Save the user's current game state
        savedInstanceState.putString("shopListTitle", shopListTitle);
        for(ShopListItem s:ShopList){
            List<TableShopListClass> item = TableShopListClass.find(TableShopListClass.class,"list_title = ? and name = ?",s.listTitle,s.name);
            if(item.isEmpty()) {
                TableShopListClass tableShopListClass = new TableShopListClass(s);
                tableShopListClass.save();
            }
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void getUserFromMainActivity(Intent intent){
        userId = intent.getStringExtra("id");
        String userName = intent.getStringExtra("name");
        String userAvatar = intent.getStringExtra("avatar");
        ImageView toolbarPhoto = (ImageView) findViewById(R.id.friends_avatar);
        Picasso.with(this)
                .load(userAvatar)
                .transform(new CircularTransformation(80))
                .into(toolbarPhoto);
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
        shopListTitle = title;
        ((TextView)findViewById(R.id.listTitle)).setText(shopListTitle);
    }

    public void FillShopList(String name, String quantity, String value) {

        ShopList.add(new ShopListItem(name, quantity, value, shopListTitle));


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            Log.d("dada","remove item in " + ShopList.get(position).listTitle + " ShopList: { name: " + ShopList.get(position).name + "}" );


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
            final VKRequest vkRequest = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, userId ,VKApiConst.MESSAGE, prepareJson.toString()));
            vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    super.onComplete(response);

                    CreateListActivty.this.finish();
                    Toast.makeText(CreateListActivty.this, R.string.send_success, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    Toast.makeText(CreateListActivty.this, R.string.internet_access_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}