package de.ameyering.wgplaner.wgplaner.utils;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class NotificationService extends FirebaseMessagingService {
    private static final String SHOPPING_LIST_ADD = "ShoppingList-Add";
    private static final String SHOPPING_LIST_UPDATE = "ShoppingList-Update";
    private static final String SHOPPING_LIST_BUY = "ShoppingList-Buy";
    private static final String GROUP_DATA = "Group-Data";
    private static final String GROUP_IMAGE = "Group-Image";
    private static final String USER_DATA = "User-Data";
    private static final String USER_IMAGE = "User-Image";
    private static final String GROUP_NEW_MEMBER = "Group-NewMember";
    private static final String GROUP_MEMBER_LEFT = "Group-MemberLeft";

    private static final String TYPE_KEY = "Type";
    private static final String UPDATED_KEY = "Updated";
    
    private static DataProviderInterface dataProvider = DataProvider.getInstance();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        if (DataProvider.isInitialized() && dataProvider.getCurrentGroupUID() != null) {

            switch (data.get(TYPE_KEY)) {
                case GROUP_DATA: {
                    dataProvider.syncGroup();
                }
                break;
                case GROUP_IMAGE: {
                    dataProvider.syncGroupPicture(getApplicationContext());
                }
                break;
                case GROUP_NEW_MEMBER: {
                    String uid = getUidsFromData(data);
                    if(uid != null && !uid.isEmpty()) {
                        dataProvider.syncGroupNewMember(uid, getApplicationContext());
                    }
                }
                break;
                case GROUP_MEMBER_LEFT: {
                    String uid = getUidsFromData(data);
                    if(uid != null && !uid.isEmpty()){
                        dataProvider.syncGroupMemberLeft(uid, getApplicationContext());
                    }
                }
                break;
                case USER_DATA: {
                    String uid = getUidsFromData(data);
                    if(uid != null && !uid.isEmpty()){
                        dataProvider.syncGroupMember(uid);
                    }
                }
                break;
                case USER_IMAGE: {
                    String uid = getUidsFromData(data);
                    if(uid != null && !uid.isEmpty()){
                        dataProvider.syncGroupMemberPicture(uid, getApplicationContext());
                    }
                }
                break;
                case SHOPPING_LIST_ADD:
                case SHOPPING_LIST_BUY:
                case SHOPPING_LIST_UPDATE: {
                    dataProvider.syncShoppingList();
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
    
    private String getUidsFromData(Map<String, String> data){
        StringBuilder uidArray = new StringBuilder(data.get(UPDATED_KEY));

        int indexArrayStart = uidArray.indexOf("[\"");
        if(indexArrayStart != -1){
            uidArray.delete(indexArrayStart, indexArrayStart + 2);
        }

        int indexArrayEnd = uidArray.lastIndexOf("\"]");
        if(indexArrayEnd != -1){
            uidArray.deleteCharAt(indexArrayEnd);
            uidArray.deleteCharAt(indexArrayEnd);
        }
        
        if(uidArray.indexOf(",") == -1){
            return uidArray.toString();
        } else {
            return "";
        }        
    }
}
