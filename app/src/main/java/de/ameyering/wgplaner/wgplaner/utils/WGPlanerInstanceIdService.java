package de.ameyering.wgplaner.wgplaner.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;

public class WGPlanerInstanceIdService extends FirebaseInstanceIdService {
    private static final String TOKEN_TAG = "TOKEN";
    private static List<TokenListener> listeners = new ArrayList<>();
    private static boolean tokenReceived = false;

    public interface TokenListener {
        void onTokenReceived();
    }

    @Override
    public void onTokenRefresh() {
        tokenReceived = true;
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TOKEN_TAG, "Refreshed token:" + refreshedToken);
        sendRegistrationToServer(refreshedToken);

        callAllListeners();
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if (refreshedToken != null) {
            DataProviderInterface dataProvider = ((WGPlanerApplication) getApplication()).getDataProviderInterface();
            dataProvider.setFirebaseInstanceId(refreshedToken, getApplicationContext());
        }
    }

    private void callAllListeners() {
        for(TokenListener listener: listeners) {
            listener.onTokenReceived();
        }
    }

    public static void addListener(TokenListener listener) {
        if(listener != null && !listeners.contains(listener)) {
            listeners.add(listener);

            if(tokenReceived) {
                listener.onTokenReceived();
            }
        }
    }

    public static void removeListener(TokenListener listener) {
        if(listener != null) {
            listeners.remove(listener);
        }
    }
}
