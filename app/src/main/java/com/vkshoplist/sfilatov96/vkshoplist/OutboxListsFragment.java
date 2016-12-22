package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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


public class OutboxListsFragment extends Fragment {
    ShopListsRecyclerViewAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        getActivity().setTitle(R.string.my_outbox_lists);
        rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        //prepareShopListsList();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_friends_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                prepareShopListsList();
            }
        });


        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
                        if(!shopListAuthor.is_performed) {
                            Intent intent = new Intent(getActivity(), ExecuteListActivity.class);
                            intent.putExtra("SHOPLIST_TITLE",shopListAuthor.title);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            Toast.makeText(getActivity(), R.string.shoplist_allready_performed, Toast.LENGTH_SHORT).show();
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
            if(t!=null &&  (!t.is_inbox_shoplist) && (!t.is_blank)) {
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


            TableShopListClass.deleteAll(TableShopListClass.class, "list_title = ?", tableShopListAuthors.get(position).title);

            List<TableShopListAuthor> author = TableShopListAuthor.find(TableShopListAuthor.class, "title = ?", tableShopListAuthors.get(position).title);
            if(!author.isEmpty()) {
                author.get(0).delete();
            }


            tableShopListAuthors.remove(position);

            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        prepareShopListsList();
    }
}
