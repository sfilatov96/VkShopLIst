package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Iterator;


public class ListsFragment extends Fragment {
    private RecyclerView recyclerView;
    ShopListRecyclerViewAdapter adapter;
    final String FRIENDS = "FRIENDS";
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
        fillShopListsListItem();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.lists_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        adapter = new ShopListRecyclerViewAdapter(getActivity(), tableShopListAuthors);
        recyclerView.setAdapter(adapter);

    }

    private void fillShopListsListItem(){
       //tableShopListAuthors = (ArrayList<TableShopListAuthor>) TableShopListAuthor.findAll(TableShopListAuthor.class);
        Iterator<TableShopListAuthor> iterator = TableShopListAuthor.findAll(TableShopListAuthor.class);
        int i = 0;
        tableShopListAuthors = new ArrayList<TableShopListAuthor>();
        while (iterator.hasNext()) {
            TableShopListAuthor t = iterator.next();
            if(t!=null) {
                Log.d("tables", t.author);
                tableShopListAuthors.add(t);
            }

        }


    }
}
