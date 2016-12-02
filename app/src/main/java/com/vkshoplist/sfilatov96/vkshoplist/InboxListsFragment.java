package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InboxListsFragment extends Fragment {
    ShopListsRecyclerViewAdapter adapter;
    ArrayList<TableShopListAuthor> tableShopListAuthors;

    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        prepareShopListsList();
        return rootView;

    }
    private void prepareShopListsList(){
        ItemTouchHelper itemTouchHelper;
        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);
        RecyclerView recyclerView;
        fillShopListsListItem();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.lists_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        adapter = new ShopListsRecyclerViewAdapter(getActivity(), tableShopListAuthors);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TableShopListAuthor shopListAuthor = adapter.getListByPosition(position);
                        if(shopListAuthor.is_inbox_shoplist) {
                            Intent intent = new Intent(getActivity(), ExecuteListActivity.class);
                            intent.putExtra("SHOPLIST_TITLE",shopListAuthor.title);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), R.string.is_outbox_shoplist, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );

        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    private void fillShopListsListItem(){
        Iterator<TableShopListAuthor> iterator = TableShopListAuthor.findAll(TableShopListAuthor.class);
        tableShopListAuthors = new ArrayList<TableShopListAuthor>();
        while (iterator.hasNext()) {
            TableShopListAuthor t = iterator.next();
            if(t!=null && t.is_inbox_shoplist) {
                Log.d("tables", t.author);
                tableShopListAuthors.add(t);
            }

        }


    }

    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT ){

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {


            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Log.d("dada","remove item in " + tableShopListAuthors.get(position).title + " ShopList: { name: " + tableShopListAuthors.get(position).author + "}" );


            List<TableShopListClass> item = TableShopListClass.find(TableShopListClass.class, "list_title = ?", tableShopListAuthors.get(position).title);

            item.clear();

            List<TableShopListAuthor> author = TableShopListAuthor.find(TableShopListAuthor.class, "title = ?", tableShopListAuthors.get(position).title);
            if(!author.isEmpty()) {
                author.get(0).delete();
            }


            tableShopListAuthors.remove(position);

            adapter.notifyDataSetChanged();
        }
    };


}
