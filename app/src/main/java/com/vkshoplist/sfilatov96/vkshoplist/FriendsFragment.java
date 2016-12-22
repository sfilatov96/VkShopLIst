package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sfilatov96 on 20.11.16.
 */

    public  class FriendsFragment extends Fragment {
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private SearchView mSearchView;
        String mSearchString;
        private RecyclerView recyclerView;
        FriendsRecyclerViewAdapter adapter;
        final String FRIENDS = "FRIENDS";
        public final String SEARCH_KEY="SEARCH_KEY";
        View rootView;

        private ArrayList<Person> friends;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            if(savedInstanceState != null){
                ArrayListSerializible arrayListSerializible;
                arrayListSerializible =  (ArrayListSerializible) savedInstanceState.getSerializable(FRIENDS);
                friends = arrayListSerializible.persons;
                mSearchString = savedInstanceState.getString(SEARCH_KEY);;
            }

            super.onCreate(savedInstanceState);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            ArrayListSerializible arrayListSerializible = new ArrayListSerializible(friends);
            outState.putSerializable(FRIENDS,arrayListSerializible);
            //mSearchView.getQuery().toString();
            //mSearchString = mSearchView.getQuery().toString();

            //outState.putString(SEARCH_KEY, mSearchString);
            super.onSaveInstanceState(outState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getActivity().setTitle(R.string.my_friends);
            rootView = inflater.inflate(R.layout.fragment_friends, container, false);
            setHasOptionsMenu(true);
            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_friends_container);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }
            });


            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
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
                            Intent intent = new Intent(getActivity(), CreateListActivity.class);
                            String id = String.valueOf(person.id);
                            intent.putExtra("id",id);
//                            intent.putExtra("name",person.name);
//                            intent.putExtra("avatar",person.avater);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
            adapter = new FriendsRecyclerViewAdapter(getActivity(), friends);
            recyclerView.setAdapter(adapter);
        }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        //focus the SearchView
        if (mSearchString != null) {
            searchMenuItem.expandActionView();
            mSearchView.setQuery(mSearchString, true);
            mSearchView.setIconified(false);
            mSearchView.requestFocus();


        }
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}


