package com.vkshoplist.sfilatov96.vkshoplist;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sfilatov96 on 08.11.16.
 */
public class ArrayListSerializible implements Serializable {
    ArrayList<Person> persons;
    public ArrayListSerializible(ArrayList<Person> persons){
        this.persons = persons;

    }
    public ArrayListSerializible(){

    }

    public ArrayList<Person> getPersons(){
        return persons;
    }
}
