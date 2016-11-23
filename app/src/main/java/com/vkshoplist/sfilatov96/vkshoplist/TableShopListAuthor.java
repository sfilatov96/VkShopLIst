package com.vkshoplist.sfilatov96.vkshoplist;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * Created by sfilatov96 on 30.10.16.
 */

public class TableShopListAuthor extends SugarRecord {
    String author;
    String title;
    boolean is_performed;

    public TableShopListAuthor(){
    }
    public TableShopListAuthor(String author,String title) {
        this.author = author;
        this.title = title;
        this.is_performed = false;
    }
}