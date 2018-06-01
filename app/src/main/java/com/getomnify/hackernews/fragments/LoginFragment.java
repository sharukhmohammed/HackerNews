package com.getomnify.hackernews.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getomnify.hackernews.R;
import com.getomnify.hackernews.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    public static final String TAG = "LoginFragment";

    /*Core Fields*/
    private Context context;
    private MainActivity activity;
    private View rootView;

    /*Login Based Fields*/
    private SharedPreferences prefs;
    private GoogleSignInClient signInClient;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        activity = (MainActivity) getActivity();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        rootView = view;

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        signInClient = GoogleSignIn.getClient(context,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build());


        //Configuring Sign-in button
        SignInButton signInButton = view.findViewById(R.id.fragment_login_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_login_button:

                Intent signInIntent = signInClient.getSignInIntent();
                startActivityForResult(signInIntent, 445);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 445) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Signed in successfully, show authenticated UI.
                handleSignIn(account);

            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
                e.printStackTrace();
                handleSignIn(null);
            }
        }

    }

    void handleSignIn(GoogleSignInAccount account)
    {
        if (account != null) {
            //SharedPreferences.Editor editor = prefs.edit();


            /*editor.putString("user_email",account.getEmail());
            editor.putString("user_first_name",account.getGivenName());
            editor.putString("user_last_name",account.getFamilyName());
            editor.putString("user_full_name",account.getDisplayName());
            editor.putString("user_id",account.getId());
            editor.putString("user_dp",account.getPhotoUrl().toString());*/

            //Currently extracting only required information.
            //If any extra information is needed, it will be available in account variable, after successful sign in

            activity.postLoginSetup();

            //TODO: Write code for sign out
            // available here > https://developers.google.com/identity/sign-in/android/disconnect

        } else Snackbar.make(rootView, "Sign in Error.", Snackbar.LENGTH_SHORT).show();
    }


}
