package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sfilatov96 on 06.11.16.
 */
public class VkHelper {
    ArrayList<Person> persons;
    Context context;
    public final String ONLINE="online";
    public final String OFFLINE="offline";
    public Listener listener;
    public LongPollListener longPollListener;
    public MessageSendListener messageSendListener;

    VkHelper(Context context){
        this.context = context;
    }
    public void findFriends() {
        final VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "photo_200,order"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                parseUser(response);

            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(context, "oshibka", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {

            }
        });


    }

    private void parseUser(VKResponse response){
        VKList<VKApiUser> users  = ((VKList<VKApiUser>) response.parsedModel);
        persons = new ArrayList<>();
        for(VKApiUser u: users){
            if(u.online){
                persons.add(new Person(u.toString(), ONLINE, u.photo_200, u.id));
            } else {
                persons.add(new Person(u.toString(),  OFFLINE, u.photo_200, u.id));
            }

        }
        listener.onAppearFriends(persons);



    }

    public void sendShopListByFriendsID(String friend_id,JSONObject prepareJson){
        final VKRequest vkRequest = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, friend_id ,VKApiConst.MESSAGE, prepareJson.toString()));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);
                messageSendListener.onComplete();


            }

            @Override
            public void onError(VKError error) {
                messageSendListener.onError();
                super.onError(error);

            }
        });
    }


    public void getProfileInNavHeader() {

        final VKRequest requestProfile = new VKRequest("account.getProfileInfo", VKParameters.from(VKApiConst.FIELDS, "first_name,last_name,email"));
        requestProfile.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);
                JSONObject jsonObject = response.json;
                String screen_name = null;

                try {
                    screen_name = jsonObject.getJSONObject("response").getString("screen_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(screen_name != null)
                    Log.d("screen_name",screen_name);
                    getProfileById(screen_name);


                //fillNavHeaderViews(userProfile);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d("profile", error.toString());
            }
        });
    }

    public void getProfileById(String screen_name){
        final VKRequest request = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS, String.format("%s",screen_name),
                VKApiConst.FIELDS, "photo_200,screen_name"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                goAway(response);

            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(context, "oshibka", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {

            }
        });

    }

    private void goAway(VKResponse response) {
        try {
            JSONObject r = response.json.getJSONArray("response").getJSONObject(0);
            listener.onAppearUserProfile(r);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void  getLongPolling(){
        final VKRequest requestProfile = new VKRequest("messages.getLongPollServer", VKParameters.from());
        requestProfile.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);
                JSONObject jsonObject = response.json;
                String key = null;
                String server = null;
                String ts = null;

                try {
                    key = jsonObject.getJSONObject("response").getString("key");
                    server = jsonObject.getJSONObject("response").getString("server");
                    ts = jsonObject.getJSONObject("response").getString("ts");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(key != null)
                longPollListener.onGetLongPoll(key, server, ts);


            }

        });
    }

    public interface Listener{
        void onAppearFriends(ArrayList<Person> persons);
        void onAppearUserProfile(JSONObject jsonObject);
    }

    public void setLongPollListener(LongPollListener longPollListener){
        this.longPollListener = longPollListener;

    }

    public interface LongPollListener{
        void onGetLongPoll(String key,String server, String ts);
    }

    public interface MessageSendListener{
        void onComplete();
        void onError();
    }



    public void setListener(Listener listener){
        this.listener = listener;
    }

    public void setMessageSendListener(MessageSendListener messageSendListener){
        this.messageSendListener = messageSendListener;
    }
}
