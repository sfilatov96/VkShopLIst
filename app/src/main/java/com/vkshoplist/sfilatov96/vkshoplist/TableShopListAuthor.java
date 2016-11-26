package com.vkshoplist.sfilatov96.vkshoplist;

import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.util.Date;

/**
 * Created by sfilatov96 on 30.10.16.
 */

public class TableShopListAuthor extends SugarRecord {
    String author;
    String title;
    Date date;
    boolean is_performed;
    boolean is_inbox_shoplist;
    boolean is_blank;
    String uservk;
    public TableShopListAuthor(){
    }
    public TableShopListAuthor(String author,String title,boolean is_inbox_shoplist, boolean is_blank, String user_id) {
        this.uservk = user_id;
        this.author = author;
        this.title = title;
        this.is_performed = false;
        this.is_inbox_shoplist = is_inbox_shoplist;
        this.is_blank = is_blank;
    }
}