package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;

public interface DataProviderInterface {

    enum SetUpState {
        UNREGISTERED, REGISTERED, SETUP_COMPLETED, GET_USER_FAILED, GET_GROUP_FAILED, CONNECTION_FAILED
    }

    SetUpState initialize(String uid, Context context);

    void registerUser(final ServerCallsInterface.OnAsyncCallListener<User> listener);

    void setFirebaseInstanceId(String token, Context context);

    String getFirebaseInstanceId();

    void setCurrentUserDisplayName(String displayName,
        final ServerCallsInterface.OnAsyncCallListener<User> listener);

    void setCurrentUserImage(Bitmap bitmap,
        final ServerCallsInterface.OnAsyncCallListener<SuccessResponse> listener);

    void setCurrentUserEmail(@Nullable String email,
        final ServerCallsInterface.OnAsyncCallListener<User> listener);

    void setCurrentUserLocale(Locale locale,
        final ServerCallsInterface.OnAsyncCallListener<User> listener);

    String getCurrentUserUid();

    String getCurrentUserDisplayName();

    Bitmap getCurrentUserImage(Context context);

    String getCurrentUserEmail();

    Locale getCurrentUserLocale();

    void setCurrentGroupName(String groupName,
        final ServerCallsInterface.OnAsyncCallListener<Group> listener);

    void setCurrentGroupCurrency(Currency currency,
        final ServerCallsInterface.OnAsyncCallListener<Group> listener);

    void setCurrentGroupImage(Bitmap bitmap,
        final ServerCallsInterface.OnAsyncCallListener<SuccessResponse> listener);

    UUID getCurrentGroupUID();

    String getCurrentGroupName();

    Currency getCurrentGroupCurrency();

    Bitmap getCurrentGroupImage(Context context);

    ArrayList<User> getCurrentGroupMembers();

    User getUserByUid(String uid);

    boolean isAdmin(String uid);

    void createGroup(String name, String groupCountry, Bitmap image, Context context,
        final ServerCallsInterface.OnAsyncCallListener<Group> listener);

    void updateGroup(Group group, final ServerCallsInterface.OnAsyncCallListener<Group> listener);

    void joinCurrentGroup(String accessKey, Context context,
        final ServerCallsInterface.OnAsyncCallListener<Group> listener);

    void leaveCurrentGroup(final ServerCallsInterface.OnAsyncCallListener<SuccessResponse> listener);

    String createGroupAccessKey();

    void addShoppingListItem(ListItem item,
        final ServerCallsInterface.OnAsyncCallListener<ListItem> listener);

    void selectShoppingListItem(ListItem item);

    void unselectShoppingListItem(ListItem item);

    boolean isItemSelected(ListItem item);

    void addPriceToListItem(ListItem item, String price,
        final ServerCallsInterface.OnAsyncCallListener<ListItem> listener);

    void buySelection(final ServerCallsInterface.OnAsyncCallListener<SuccessResponse> listener);

    ListItem getListItem(UUID uuid);

    ArrayList<ListItem> getCurrentShoppingList();

    boolean isSomethingSelected();

    ArrayList<ListItem> getBoughtItems();

    ApiResponse<ShoppingList> syncShoppingList();

    void syncGroup();

    void syncGroupMembers();

    void syncGroupNewMember(String uid, Context context);

    void syncGroupMemberLeft(String uid, Context context);

    void syncGroupMember(String uid);

    void syncGroupMemberPicture(String uid, Context context);

    void syncGroupPicture(Context context);

    void syncBoughtItems();
}
