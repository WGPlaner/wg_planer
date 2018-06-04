package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.BillList;
import io.swagger.client.model.Group;
import io.swagger.client.model.GroupCode;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;
import io.swagger.client.Configuration;

public abstract class ServerCallsInterface {

    public static ServerCallsInterface getInstance() {
        return new ServerCalls(
                Configuration.getDefaultApiClient(),
                "https://api.wgplaner.ameyering.de"
            );
    }

    public static  ServerCallsInterface getInstance(ApiClient client, String basePath) {
        return new ServerCalls(
                client,
                basePath
            );
    }

    public abstract void setCurrentUserUid(@NonNull String uid);

    public abstract boolean isInitialized();

    public abstract void createUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener);

    public abstract ApiResponse<User> createUser(User user);

    public abstract void getUserAsync(String uid, @Nullable final OnAsyncCallListener<User> listener);

    public abstract ApiResponse<User> getUser(String uid);

    public abstract void updateUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener);

    public abstract ApiResponse<User> updateUser(User user);

    public abstract void getUserImageAsync(String uid, @Nullable final OnAsyncCallListener<byte[]> listener);

    public abstract ApiResponse<byte[]> getUserImage(String uid);

    public abstract void updateUserImageAsync(File image,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    public abstract ApiResponse<SuccessResponse> updateUserImage(File image);

    public abstract void leaveGroupAsync(@Nullable final OnAsyncCallListener<SuccessResponse> listener);

    public abstract ApiResponse<SuccessResponse> leaveGroup();

    public abstract void getGroupAsync(@Nullable final OnAsyncCallListener<Group> listener);

    public abstract ApiResponse<Group> getGroup();

    public abstract void updateGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener);

    public abstract ApiResponse<Group> updateGroup(Group group);

    public abstract void createGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener);

    public abstract ApiResponse<Group> createGroup(Group group);

    public abstract void joinGroupAsync(String accessKey, @Nullable final OnAsyncCallListener<Group> listener);

    public abstract ApiResponse<Group> joinGroup(String accessKey);

    public abstract void createGroupKeyAsync(@Nullable final OnAsyncCallListener<GroupCode> listener);

    public abstract ApiResponse<GroupCode> createGroupKey();

    public abstract void getGroupImageAsync(@Nullable final OnAsyncCallListener<byte[]> listener);

    public abstract ApiResponse<byte[]> getGroupImage();

    public abstract void updateGroupImageAsync(File image,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    public abstract ApiResponse<SuccessResponse> updateGroupImage(File image);

    public abstract void getShoppingListAsync(@Nullable final OnAsyncCallListener<ShoppingList> listener);

    public abstract ApiResponse<ShoppingList> getShoppingList();

    public abstract void createShoppingListItemAsync(ListItem item,
        @Nullable final OnAsyncCallListener<ListItem> listener);

    public abstract ApiResponse<ListItem> createShoppingListItem(ListItem item);

    public abstract void updateShoppingListItemAsync(ListItem item,
        @Nullable final OnAsyncCallListener<ListItem> listener);

    public abstract ApiResponse<ListItem> updateShoppingListItem(ListItem item);

    public abstract void buyListItemsAsync(List<UUID> items,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    public abstract ApiResponse<SuccessResponse> buyListItems(List<UUID> items);

    public abstract void getBillListAsync(@Nullable final OnAsyncCallListener<BillList> listener);

    public abstract ApiResponse<BillList> getBillList();

    public abstract void getBoughtItemsAsync(String uid,
        @Nullable final OnAsyncCallListener<ShoppingList> listener);

    public abstract ApiResponse<ShoppingList> getBoughtItems(String uid);
}
