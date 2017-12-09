package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.GroupCode;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;

public interface ServerCallsInterface {

    interface OnAsyncCallListener<T> {
        void onFailure(ApiException e);

        void onSuccess(T result);
    }

    void createUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener);

    ApiResponse<User> createUser(User user);

    void getUserAsync(String uid, @Nullable final OnAsyncCallListener<User> listener);

    ApiResponse<User> getUser(String uid);

    void updateUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener);

    ApiResponse<User> updateUser(User user);

    void getUserImageAsync(String uid, @Nullable final OnAsyncCallListener<byte[]> listener);

    ApiResponse<byte[]> getUserImage(String uid);

    void updateUserImageAsync(File image,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    ApiResponse<SuccessResponse> updateUserImage(File image);

    void leaveGroupAsync(@Nullable final OnAsyncCallListener<SuccessResponse> listener);

    ApiResponse<SuccessResponse> leaveGroup();

    void getGroupAsync(UUID groupUid, @Nullable final OnAsyncCallListener<Group> listener);

    ApiResponse<Group> getGroup(UUID groupUid);

    void updateGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener);

    ApiResponse<Group> updateGroup(Group group);

    void createGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener);

    ApiResponse<Group> createGroup(Group group);

    void joinGroupAsync(String accessKey, @Nullable final OnAsyncCallListener<Group> listener);

    ApiResponse<Group> joinGroup(String accessKey);

    void createGroupKeyAsync(@Nullable final OnAsyncCallListener<GroupCode> listener);

    ApiResponse<GroupCode> createGroupKey();

    void getGroupImageAsync(@Nullable final OnAsyncCallListener<byte[]> listener);

    ApiResponse<byte[]> getGroupImage();

    void updateGroupImageAsync(File image,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    ApiResponse<SuccessResponse> updateGroupImage(File image);

    void getShoppingListAsync(@Nullable final OnAsyncCallListener<ShoppingList> listener);

    ApiResponse<ShoppingList> getShoppingList();

    void createShoppingListItemAsync(ListItem item,
        @Nullable final OnAsyncCallListener<ListItem> listener);

    ApiResponse<ListItem> createShoppingListItem(ListItem item);

    void updateShoppingListItemAsync(ListItem item,
        @Nullable final OnAsyncCallListener<ListItem> listener);

    ApiResponse<ListItem> updateShoppingListItem(ListItem item);

    void buyListItemsAsync(List<UUID> items,
        @Nullable final OnAsyncCallListener<SuccessResponse> listener);

    ApiResponse<SuccessResponse> buyListItems(List<UUID> items);
}
