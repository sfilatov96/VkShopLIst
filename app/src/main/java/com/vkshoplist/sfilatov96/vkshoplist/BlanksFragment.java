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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BlanksFragment extends Fragment {
    ShopListsRecyclerViewAdapter adapter;

    ArrayList<TableShopListAuthor> tableShopListAuthors;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        getActivity().setTitle(R.string.samples);
        rootView = inflater.inflate(R.layout.fragment_lists, container, false);
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
                        final TableShopListAuthor shopListAuthor = adapter.getListByPosition(position);


                        Intent intent = new Intent(getActivity(), CreateListActivity.class);
                        intent.putExtra("id",shopListAuthor.uservk);
                        intent.putExtra("shopListTitle",shopListAuthor.title);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);


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
            if(t!=null &&  (!t.is_inbox_shoplist) && (t.is_blank)) {
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

    @Override
    public void onResume() {
        super.onResume();
        prepareShopListsList();
    }
}