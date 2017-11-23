package de.ameyering.wgplaner.wgplaner.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.swagger.client.model.User;


public class WGPlanerInstanceIdService extends FirebaseInstanceIdService {
    private static final String TOKEN_TAG = "TOKEN";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TOKEN_TAG, "Refreshed token:" + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if (refreshedToken != null) {
            User user = DataProvider.Users.getCurrentUser();
            user.setFirebaseInstanceId(refreshedToken);
            DataProvider.Users.setCurrentUser(user);
            DataProvider.Users.synchronizeCurrentUser(null);
        }
    }
}
