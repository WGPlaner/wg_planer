package de.ameyering.wgplaner.wgplaner.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class NotificationService extends FirebaseMessagingService {
    private static final String SHOPPING_LIST = "ShoppingList";
    private static final String GROUP = "Group";
    private static final String USER = "User";
    private static final String GROUP_NEW_MEMBER = "GroupNewMember";

    private static final String TYPE_KEY = "Type";
    private static final String UPDATED_KEY = "Updated";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        if(DataProvider.isInitialized()) {

            switch (data.get(TYPE_KEY)) {
                case GROUP_NEW_MEMBER:
                case USER:
                case GROUP: {
                    DataProvider.getInstance().syncGroup();
                }
                break;
                case SHOPPING_LIST: {
                    DataProvider.getInstance().syncShoppingList();
                }
                break;
            }
        }

        Log.d("Messaging", "Messaging:success");
        Log.d("Data", data.toString());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
