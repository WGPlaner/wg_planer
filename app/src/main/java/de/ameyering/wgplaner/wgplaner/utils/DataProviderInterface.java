package de.ameyering.wgplaner.wgplaner.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import io.swagger.client.ApiResponse;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.User;

public interface DataProviderInterface {

    enum SetUpState{
        UNREGISTERED, REGISTERED, SETUP_COMPLETED, GET_USER_FAILED, GET_GROUP_FAILED, CONNECTION_FAILED
    }

    SetUpState initialize(String uid);

    boolean registerUser();
    
    void setFirebaseInstanceId(String token);
    
    String getFirebaseInstanceId();
    
    void setCurrentUserDisplayName(String displayName);
    
    void setCurrentUserImage(Bitmap bitmap);
    
    void setCurrentUserEmail(@Nullable String email);
    
    void setCurrentUserLocale(Locale locale);
    
    String getCurrentUserUid();
    
    String getCurrentUserDisplayName();
    
    Bitmap getCurrentUserImage(Context context);
    
    String getCurrentUserEmail();
    
    Locale getCurrentUserLocale();
    
    void setCurrentGroupName(String groupName);
    
    void setCurrentGroupCurrency(Currency currency);
    
    void setCurrentGroupImage(File image);

    UUID getCurrentGroupUID();
    
    String getCurrentGroupName();
    
    Currency getCurrentGroupCurrency();
    
    File getCurrentGroupImage();
    
    ArrayList<User> getCurrentGroupMembers();

    User getUserByUid(String uid);
    
    boolean isAdmin(String uid);

    boolean createGroup(String name, Currency currency, Bitmap image);

    boolean joinCurrentGroup(String accessKey);

    boolean leaveCurrentGroup();
    
    boolean addShoppingListItem(ListItem item);

    void selectShoppingListItem(ListItem item);

    void unselectShoppingListItem(ListItem item);

    boolean isItemSelected(ListItem item);

    void buySelection();

    ArrayList<ListItem> getCurrentShoppingList();

    boolean isSomethingSelected();

    void syncShoppingList();

    void syncGroup();
}
