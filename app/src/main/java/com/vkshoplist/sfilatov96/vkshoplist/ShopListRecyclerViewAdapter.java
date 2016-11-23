package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sfilatov96 on 20.11.16.
 */
public class ShopListRecyclerViewAdapter extends RecyclerView.Adapter<ShopListRecyclerViewAdapter.PersonViewHolder>{
    Context context;
    ArrayList<TableShopListAuthor> tableShopListAuthors;
    ArrayList<TableShopListAuthor> mDataSet;
    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView list_title;
        TextView author;


        PersonViewHolder(View itemView) {

            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_lists);
            list_title = (TextView)itemView.findViewById(R.id.shop_list_title);
            author = (TextView)itemView.findViewById(R.id.buyer_name);
            Log.d("pvh",itemView.toString());

        }
    }

    ShopListRecyclerViewAdapter(Context context, ArrayList<TableShopListAuthor> tableShopListAuthors){
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
        personViewHolder.author.setText(tableShopListAuthors.get(i).author);
        personViewHolder.list_title.setText(tableShopListAuthors.get(i).title);

    }

    @Override
    public int getItemCount() {
        if (tableShopListAuthors != null) {
            return tableShopListAuthors.size();
        } else return 0;
    }

}
