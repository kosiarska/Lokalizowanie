package pl.mt.lokalizowanie.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.mt.lokalizowanie.MapsActivity;
import pl.mt.lokalizowanie.R;
import timber.log.Timber;

public class FacebookLoginFragment extends FacebookFragment {

    private static final String TAG = FacebookLoginFragment.class.getSimpleName();

    @InjectView(R.id.authButton)
    LoginButton authButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);
        ButterKnife.inject(this, view);
        authButton.setFragment(this);
        authButton.setPublishPermissions(Arrays.asList("publish_actions"));
        return view;
    }

    boolean mapStarted = false;

    @Override
    public void onResume() {
        super.onResume();
        mapStarted = false;
    }

    @SuppressWarnings("unused")
    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Timber.i(TAG, "Logged in...");
            if(!mapStarted) {
                mapStarted = true;
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        } else if (state.isClosed()) {
            Timber.i(TAG, "Logged out...");
        }
    }
}
