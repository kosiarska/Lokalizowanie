package pl.mt.lokalizowanie.fragments;


import android.os.Bundle;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pl.mt.lokalizowanie.R;

public class FacebookShareFragment extends FacebookFragment {

    SweetAlertDialog sweetAlertDialog;

    @Override
    public void onResume() {
        super.onResume();
        postLocationToFacebook();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sweetAlertDialog != null) {
            sweetAlertDialog.cancel();
        }
    }


    protected void postLocationToFacebook() {
        String address = getArguments().getString(getResources().getString(R.string.address_result));
        address = String.format(getResources().getString(R.string.location_format), address);
        if (FacebookDialog.canPresentShareDialog(getActivity(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            // Publish the post using the Share Dialog

            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity()).setName("Facebook SDK for Android")
                    .setLink("https://developers.facebook.com/android").setPicture("https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png").setCaption("Build great social apps and get more installs.").setDescription(address).build();
            uiHelper.trackPendingDialogCall(shareDialog.present());

        } else {
            //Fallback. For example, publish the post using the Feed Dialog
            publishFeedDialog(address);
        }
    }

    private void publishFeedDialog(String address) {
        Bundle params = new Bundle();
        params.putString("name", "Facebook SDK for Android");
        params.putString("caption", "Build great social apps and get more installs.");
        params.putString("description", address);
        params.putString("link", "https://developers.facebook.com/android");
        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

        WebDialog feedDialog = (
                new WebDialog.FeedDialogBuilder(getActivity(),
                        Session.getActiveSession(),
                        params))
                .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                    @Override
                    public void onComplete(Bundle values, FacebookException error) {
                        if (error == null) {
                            // When the story is posted, echo the success
                            // and the post Id.
                            final String postId = values.getString("post_id");
                            if (postId != null) {
                                successMessage();
                            } else {
                                // User clicked the Cancel button
                                errorMessage(getResources().getString(R.string.publish_cancelled));
                            }
                        } else if (error instanceof FacebookOperationCanceledException) {
                            // User clicked the "x" button
                            errorMessage(getResources().getString(R.string.publish_cancelled));
                        } else {
                            // Generic, ex: network error
                            errorMessage(getResources().getString(R.string.something_went_wrong_message));
                        }
                    }
                })
                .build();
        feedDialog.show();
    }

    private void successMessage() {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getResources().getString(R.string.good_job))
                .setContentText(getResources().getString(R.string.location_shared)).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(FacebookShareFragment.this).commit();

                    }
                });
        sweetAlertDialog.show();
    }

    private void errorMessage(String message) {
        sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getResources().getString(R.string.something_went_wrong_title))
                .setContentText(message).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(FacebookShareFragment.this).commit();
                    }
                });
        sweetAlertDialog.show();
    }

    @Override
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
    }
}
