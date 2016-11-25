package com.vkshoplist.sfilatov96.vkshoplist;

import java.io.Serializable;
import java.util.ArrayList;


public class ArrayListSerializible implements Serializable {
    ArrayList<Person> persons;
    public ArrayListSerializible(ArrayList<Person> persons){
        this.persons = persons;

    }


    //public ArrayList<Person> getPersons(){
    //    return persons;
    //}
}
