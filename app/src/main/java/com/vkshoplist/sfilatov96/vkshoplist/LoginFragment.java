package com.vkshoplist.sfilatov96.vkshoplist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;



public class LoginFragment extends Fragment {
    SharedPreferences secretKey;
    final String APP_PREFERENCES = "SECRET_WORD";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        getActivity().setTitle(R.string.login_through_vk);
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        secretKey = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Button loginButton = (Button)rootView.findViewById(R.id.login_button);
        final EditText SecretWord = (EditText) rootView.findViewById(R.id.secret_word);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! SecretWord.getText().toString().isEmpty()){
                    String secret = SecretWord.getText().toString();
                    SharedPreferences.Editor editor = secretKey.edit();
                    editor.putString("KEY", secret);
                    editor.apply();

                    VKSdk.login(getActivity(), VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL);
                }
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event

}
