package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
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

    abstract void setCurrentUserUid(@NonNull String uid);

    abstract boolean isInitialized();

    abstract void createUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener);

    abstract ApiResponse<User> createUser(User user);

    abstract void getUserAsync(String uid, @Nullable final OnAsyncCallListener<User> listener);

    abstract ApiResponse<User> getUser(String uid);

    abstract void updateUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener);

    abstract ApiResponse<User> updateUser(User user);

    abstract void getUserImageAsync(String uid, @Nullable final OnAsyncCallListener<byte[]> listener);

    abstract ApiResponse<byte[]> getUserImage(String uid);

    abstract void updateUserImageAsync(File image,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    abstract ApiResponse<SuccessResponse> updateUserImage(File image);

    abstract void leaveGroupAsync(@Nullable final OnAsyncCallListener<SuccessResponse> listener);

    abstract ApiResponse<SuccessResponse> leaveGroup();

    abstract void getGroupAsync(@Nullable final OnAsyncCallListener<Group> listener);

    abstract ApiResponse<Group> getGroup();

    abstract void updateGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener);

    abstract ApiResponse<Group> updateGroup(Group group);

    abstract void createGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener);

    abstract ApiResponse<Group> createGroup(Group group);

    abstract void joinGroupAsync(String accessKey, @Nullable final OnAsyncCallListener<Group> listener);

    abstract ApiResponse<Group> joinGroup(String accessKey);

    abstract void createGroupKeyAsync(@Nullable final OnAsyncCallListener<GroupCode> listener);

    abstract ApiResponse<GroupCode> createGroupKey();

    abstract void getGroupImageAsync(@Nullable final OnAsyncCallListener<byte[]> listener);

    abstract ApiResponse<byte[]> getGroupImage();

    abstract void updateGroupImageAsync(File image,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    abstract ApiResponse<SuccessResponse> updateGroupImage(File image);

    abstract void getShoppingListAsync(@Nullable final OnAsyncCallListener<ShoppingList> listener);

    abstract ApiResponse<ShoppingList> getShoppingList();

    abstract void createShoppingListItemAsync(ListItem item,
        @Nullable final OnAsyncCallListener<ListItem> listener);

    abstract ApiResponse<ListItem> createShoppingListItem(ListItem item);

    abstract void updateShoppingListItemAsync(ListItem item,
        @Nullable final OnAsyncCallListener<ListItem> listener);

    abstract ApiResponse<ListItem> updateShoppingListItem(ListItem item);

    abstract void buyListItemsAsync(List<UUID> items,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    abstract ApiResponse<SuccessResponse> buyListItems(List<UUID> items);

    abstract void getBoughtItemsAsync(String uid,
        @Nullable final OnAsyncCallListener<ShoppingList> listener);

    abstract ApiResponse<ShoppingList> getBoughtItems(String uid);
}
