package com.vkshoplist.sfilatov96.vkshoplist;

/**
 * Created by sfilatov96 on 18.10.16.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.PersonViewHolder> {
    Bitmap bitmap;
    Context context;
    public final String ONLINE="online";
    ArrayList<Person> mDataSet;
    ArrayList<Person> persons;
    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView name;
        TextView is_online;
        ImageView personPhoto;


        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            name = (TextView)itemView.findViewById(R.id.person_name);
            is_online = (TextView)itemView.findViewById(R.id.online_status);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }



    FriendsRecyclerViewAdapter(Context context, ArrayList<Person> persons){
        this.persons = persons;
        mDataSet = this.persons;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.name.setText(persons.get(i).name);
        personViewHolder.is_online.setText(persons.get(i).is_online);
        if(persons.get(i).is_online == ONLINE){
            personViewHolder.is_online.setTextColor(context.getResources().getColor(R.color.online));
        } else {
            personViewHolder.is_online.setText("");
        }
        //Log.d("context",getC)
        Picasso.with(context).load(persons.get(i).avater).transform(new CircularTransformation(100)).placeholder(R.drawable.user_placeholder).into(personViewHolder.personPhoto);
        //personViewHolder.personPhoto.setImageBitmap();
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public Person getIdByPosition(int pos){
        Person p = persons.get(pos);
        return p;

    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        persons = new ArrayList<>();
        if(charText.length() == 0){
            persons.addAll(mDataSet);

        }
        else {
            for(Person item: mDataSet){
                if(item.name.toLowerCase(Locale.getDefault()).contains(charText.toLowerCase())){
                    persons.add(item);

                }
            }
        }
        notifyDataSetChanged();

    }
    public void getOnline(){}



}
