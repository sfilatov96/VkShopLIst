package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BlanksFragment extends Fragment {
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

        getActivity().setTitle(R.string.samples);
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
                        final TableShopListAuthor shopListAuthor = adapter.getListByPosition(position);
                        VkHelper vkHelper = new VkHelper(getActivity());
                        vkHelper.getProfileById(shopListAuthor.uservk);
                        vkHelper.setListener(new VkHelper.Listener() {
                            @Override
                            public void onAppearFriends(ArrayList<Person> persons) {

                            }

                            @Override
                            public void onAppearUserProfile(JSONObject jsonObject) {
                                Intent intent = new Intent(getActivity(), CreateListActivity.class);
                                intent.putExtra("id",shopListAuthor.uservk);
                                try {
                                    intent.putExtra("name",jsonObject.getString("first_name")+' '+jsonObject.getString("last_name"));
                                    intent.putExtra("avatar",jsonObject.getString("photo_200"));
                                    intent.putExtra("shopListTitle",shopListAuthor.title);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                startActivity(intent);
                            }
                        });


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