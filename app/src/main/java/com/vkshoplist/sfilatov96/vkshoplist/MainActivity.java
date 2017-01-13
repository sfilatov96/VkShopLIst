package com.vkshoplist.sfilatov96.vkshoplist;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SearchView mSearchView;
    String mSearchString;
    Fragments currentFragment;
    Tracker mTracker;
    LoginFragment loginFragment = new LoginFragment();
    FriendsFragment friendsFragment = new FriendsFragment();
    InboxListsFragment inboxListsFragment = new InboxListsFragment();
    OutboxListsFragment outboxListsFragment = new OutboxListsFragment();

    BlanksFragment blanksFragment = new BlanksFragment();
    AboutUsFragment aboutUsFragment = new AboutUsFragment();

    private final String NO_INTERNET_ACCESS = "Not connected to Internet";
    private final String APP_PREFERENCES = "LONG_POLL_SERVER";

    enum Fragments implements Serializable{
        LoginFragment,FriendsFragment,InboxFragment,OutboxFragment,BlanksFragment,Nothing,AboutUsFragment
    }


    SharedPreferences LastLongPollServer;




    public final String SEARCH_KEY="SEARCH_KEY";
    private BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();

            String message = b.getString("message");

            if (message == NO_INTERNET_ACCESS) {
                Intent i = new Intent(MainActivity.this, VkMessangerService.class);
                stopService(i);
            } else {

            }
        }
    };

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
                currentFragment = (Fragments) savedInstanceState.getSerializable("CURRENT_FRAGMENT");
            }


            if (VKSdk.isLoggedIn()) {
                if (currentFragment == null) {
                    showFragment(Fragments.FriendsFragment);
                } else {
                    showFragment(currentFragment);

                }
                getProfileFromVk();


            } else {
                showFragment(Fragments.LoginFragment);

            }
            LastLongPollServer = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putSerializable("CURRENT_FRAGMENT", currentFragment);
        }

        private void runService(String key, String server, String ts) {
            Intent intent = new Intent(this, VkMessangerService.class);
            intent.putExtra("KEY", key);
            intent.putExtra("SERVER", server);
            intent.putExtra("TS", ts);
            startService(intent);
        }

        private void offService() {
            Intent intent = new Intent(this, VkMessangerService.class);
            stopService(intent);
        }

        private void showFragment(Fragments fragments) {
            if (VKSdk.isLoggedIn() || fragments == Fragments.LoginFragment) {
                if (friendsFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(friendsFragment).addToBackStack(null).commitAllowingStateLoss();
                }
                if (loginFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(loginFragment).addToBackStack(null).commitAllowingStateLoss();
                }
                if (inboxListsFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(inboxListsFragment).addToBackStack(null).commitAllowingStateLoss();
                }
                if (outboxListsFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(outboxListsFragment).addToBackStack(null)
                            .commitAllowingStateLoss();
                }
                if (blanksFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(blanksFragment).addToBackStack(null)
                            .commitAllowingStateLoss();
                }
                if (aboutUsFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(aboutUsFragment).addToBackStack(null)
                            .commitAllowingStateLoss();
                }


                currentFragment = fragments;
                switch (fragments) {
                    case FriendsFragment:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.friends_fragment, friendsFragment)
                                .commitAllowingStateLoss();
                        break;
                    case InboxFragment:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.lists_fragment, inboxListsFragment)
                                .commitAllowingStateLoss();
                        break;
                    case OutboxFragment:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.lists_fragment, outboxListsFragment)
                                .commitAllowingStateLoss();
                        break;
                    case LoginFragment:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.login_fragment, loginFragment)
                                .commitAllowingStateLoss();
                        break;
                    case BlanksFragment:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.lists_fragment, blanksFragment)
                                .commitAllowingStateLoss();
                        break;
                    case AboutUsFragment:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.about_us_fragment, aboutUsFragment)
                                .commitAllowingStateLoss();
                    default:
                        break;

                }
            } else {
                Toast.makeText(this,R.string.not_logged_in,Toast.LENGTH_LONG).show();
            }
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


            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            //int id = item.getItemId();

            //noinspection SimplifiableIfStatement


            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();
            if (id == R.id.nav_my_outbox_lists) {
                showFragment(Fragments.OutboxFragment);
                // Handle the camera action
            } else if (id == R.id.nav_my_inbox_lists) {
                showFragment(Fragments.InboxFragment);
            } else if (id == R.id.nav_friends) {
                showFragment(Fragments.FriendsFragment);
            } else if (id == R.id.nav_blanks) {
                showFragment(Fragments.BlanksFragment);
            } else if (id == R.id.nav_about) {
                showFragment(Fragments.AboutUsFragment);
            } else if (id == R.id.nav_logout) {
                if (VKSdk.isLoggedIn()) {
                    VKSdk.logout();
                    offService();
                    showFragment(Fragments.LoginFragment);

                }

            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }


        private void getProfileFromVk() {
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
            if ((LastLongPollServer != null) && (!LastLongPollServer.getString("KEY", "").isEmpty())) {
                runService(LastLongPollServer.getString("KEY", ""),
                        LastLongPollServer.getString("SERVER", ""),
                        LastLongPollServer.getString("TS", ""));
            } else {
                vkHelper.getLongPolling();
                vkHelper.setLongPollListener(new VkHelper.LongPollListener() {
                    @Override
                    public void onGetLongPoll(String key, String server, String ts) {
                        runService(key, server, ts);
                    }
                });
            }
        }

        public void fillNavHeaderViews(JSONObject userProfile) {
            TextView profile_name = (TextView) findViewById(R.id.profile_name);
            TextView profile_email = (TextView) findViewById(R.id.profile_email);
            ImageView profile_photo = (ImageView) findViewById(R.id.profile_photo);

            try {
                String login_name = userProfile.getString("first_name") + " " + userProfile.getString("last_name");
                profile_name.setText(login_name);
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
                    showFragment(Fragments.FriendsFragment);
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

        @Override
        protected void onResume() {
            super.onResume();

            registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));
        }

        @Override
        protected void onPause() {
            super.onPause();
            unregisterReceiver(broadcastReceiver);
        }


}
