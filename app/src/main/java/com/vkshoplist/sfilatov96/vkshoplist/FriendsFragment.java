package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    RVAdapter adapter;
    final String FRIENDS = "FRIENDS";
    View rootView;

    private ArrayList<Person> friends;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            ArrayListSerializible arrayListSerializible;
            arrayListSerializible =  (ArrayListSerializible) savedInstanceState.getSerializable(FRIENDS);
            friends = arrayListSerializible.persons;

        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayListSerializible arrayListSerializible = new ArrayListSerializible(friends);
        outState.putSerializable(FRIENDS,arrayListSerializible);
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        prepareFriendsAndProfile();





        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event



    private void prepareFriendsAndProfile(){

        recyclerView = (RecyclerView)rootView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Person person = adapter.getIdByPosition(position);
                        Intent intent = new Intent(getActivity(), CreateListActivty.class);
                        String id = String.valueOf(person.id);
                        intent.putExtra("id",id);
                        intent.putExtra("name",person.name);
                        intent.putExtra("avatar",person.avater);
                        startActivity(intent);
                    }
                })
        );
        if (friends == null) {
            VkHelper vkHelper = new VkHelper(getActivity());
            vkHelper.findFriends();
            vkHelper.setListener(new VkHelper.Listener() {
                @Override
                public void onAppearFriends(ArrayList<Person> persons) {
                    friends = persons;
                    fillRecycleView();

                }

                @Override
                public void onAppearUserProfile(JSONObject jsonObject) {

                }
            });
        } else {
            fillRecycleView();
        }

    }
    public void fillRecycleView(){
        adapter = new RVAdapter(getActivity(), friends);
        recyclerView.setAdapter(adapter);
    }






}
