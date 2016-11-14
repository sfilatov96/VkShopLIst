package com.vkshoplist.sfilatov96.vkshoplist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SearchView mSearchView;
    String mSearchString;

    Tracker mTracker;
    LoginFragment loginFragment = new LoginFragment();
    FriendsFragment friendsFragment = new FriendsFragment();


    public final String VKUSERID="VkUserId";
    public final String SEARCH_KEY="SEARCH_KEY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        VkApplication application = (VkApplication) getApplication();
        mTracker = application.getDefaultTracker();

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_KEY);
        }


        if ( VKSdk.isLoggedIn()) {

            showFriendsFragment();
            getProfileFromVk();



        } else {
            showLoginFragment();

        }




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSearchString = mSearchView.getQuery().toString();
        outState.putString(SEARCH_KEY, mSearchString);
    }

    private void runService(String key, String server, String ts){
        Intent intent = new Intent(this,VkMessangerService.class);
        intent.putExtra("KEY",key);
        intent.putExtra("SERVER",server);
        intent.putExtra("TS",ts);
        startService(intent);
    }

    private void showLoginFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment, loginFragment)
                .commit();

    }
    private void hideLoginFragment(){

        getSupportFragmentManager().beginTransaction()
                .remove(loginFragment)
                .commit();
    }
    private void showFriendsFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.friends_fragment, friendsFragment)
                .commit();
    }

    private void hideFriendsFragment(){
        getSupportFragmentManager().beginTransaction()
                .remove(friendsFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        /*MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }*/

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        //focus the SearchView
        if(mSearchString != null && !mSearchString.isEmpty()){
            searchMenuItem.expandActionView();
            mSearchView.setQuery(mSearchString, true);

        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                friendsFragment.adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friendsFragment.adapter.filter(newText);
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            List<TableShopListClass> allContacts = TableShopListClass.listAll(TableShopListClass.class);
            TableShopListClass.deleteAll(TableShopListClass.class);


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            if(VKSdk.isLoggedIn()){
                VKSdk.logout();
                hideFriendsFragment();
                showLoginFragment();

            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void getProfileFromVk(){
        VkHelper vkHelper = new VkHelper(this);
        vkHelper.getProfileInNavHeader();
        vkHelper.setListener(new VkHelper.Listener() {
            @Override
            public void onAppearUserProfile(JSONObject jsonObject) {
                fillNavHeaderViews(jsonObject);
            }

            @Override
            public void onAppearFriends(ArrayList<Person> persons) {

            }
        });
        vkHelper.getLongPolling();
        vkHelper.setLongPollListener(new VkHelper.LongPollListener() {
            @Override
            public void onGetLongPoll(String key, String server, String ts) {
                runService(key,server,ts);
            }
        });
    }

    public void fillNavHeaderViews(JSONObject userProfile) {
        TextView profile_name = (TextView) findViewById(R.id.profile_name);
        TextView profile_email = (TextView) findViewById(R.id.profile_email);
        ImageView profile_photo = (ImageView) findViewById(R.id.profile_photo);

        try {
            profile_name.setText(userProfile.getString("first_name") + " " + userProfile.getString("last_name"));
            profile_email.setText(userProfile.getString("screen_name"));
            Picasso.with(this).load(userProfile.getString("photo_200")).transform(new CircularTransformation(100)).placeholder(R.drawable.user_placeholder).into(profile_photo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {

                Toast.makeText(MainActivity.this, R.string.auth_success, Toast.LENGTH_LONG).show();
                hideLoginFragment();
                showFriendsFragment();
                getProfileFromVk();



            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, R.string.auth_error, Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }


}
