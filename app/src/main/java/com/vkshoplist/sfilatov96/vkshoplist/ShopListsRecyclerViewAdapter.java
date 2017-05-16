package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by sfilatov96 on 20.11.16.
 */
public class ShopListsRecyclerViewAdapter extends RecyclerView.Adapter<ShopListsRecyclerViewAdapter.PersonViewHolder>{
    Context context;
    ArrayList<TableShopListAuthor> tableShopListAuthors;
    ArrayList<TableShopListAuthor> mDataSet;
    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView list_title;
        TextView author;
        TextView list_date;
        TextView is_performed;


        PersonViewHolder(View itemView) {

            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_lists);
            is_performed = (TextView)itemView.findViewById(R.id.is_performed);
            list_title = (TextView)itemView.findViewById(R.id.shop_list_title);
            list_date = (TextView)itemView.findViewById(R.id.shop_list_date);
            author = (TextView)itemView.findViewById(R.id.buyer_name);
            Log.d("pvh",itemView.toString());

        }
    }

    ShopListsRecyclerViewAdapter(Context context, ArrayList<TableShopListAuthor> tableShopListAuthors){
        Collections.reverse(tableShopListAuthors);
        this.tableShopListAuthors = tableShopListAuthors;
        mDataSet = this.tableShopListAuthors;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lists_shop_list_item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        String sender_or_receiver;
        String[] date_and_title = tableShopListAuthors.get(i).title.split(" - ");
        if (tableShopListAuthors.get(i).is_secret) {
            personViewHolder.list_title.setText(date_and_title[0]+"(приватный)");
        } else {
            personViewHolder.list_title.setText(date_and_title[0]);
        }
        personViewHolder.list_date.setText(date_and_title[1]);
        if(tableShopListAuthors.get(i).is_performed){
            personViewHolder.cv.setAlpha(((float) 0.35));
            personViewHolder.is_performed.setText(R.string.completed);
        }
        if(tableShopListAuthors.get(i).is_inbox_shoplist){
            sender_or_receiver = "Отправитель: " + tableShopListAuthors.get(i).author;
        } else {
            sender_or_receiver = "Получатель: " + tableShopListAuthors.get(i).author;
        }
        personViewHolder.author.setText(sender_or_receiver);

    }

    @Override
    public int getItemCount() {
        if (tableShopListAuthors != null) {
            return tableShopListAuthors.size();
        } else return 0;
    }

    public TableShopListAuthor getListByPosition(int pos){
        TableShopListAuthor ta  = tableShopListAuthors.get(pos);
        return ta;

    }



}
