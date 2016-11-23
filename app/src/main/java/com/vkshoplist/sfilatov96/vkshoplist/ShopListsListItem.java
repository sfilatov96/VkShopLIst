package com.vkshoplist.sfilatov96.vkshoplist;

/**
 * Created by sfilatov96 on 20.11.16.
 */
public class ShopListsListItem {
    String author;
    String title;
    boolean is_performed;

    ShopListsListItem() {

    }

    ShopListsListItem(String author, String title) {
        this.author = author;
        this.title = title;
        this.is_performed = false;
    }
}
