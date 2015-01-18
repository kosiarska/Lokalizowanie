package pl.mt.lokalizowanie;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import pl.mt.lokalizowanie.fragments.FacebookLoginFragment;


public class StartActivity extends FragmentActivity {

    private FacebookLoginFragment facebookLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            facebookLoginFragment = new FacebookLoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, facebookLoginFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            facebookLoginFragment = (FacebookLoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

    }
}