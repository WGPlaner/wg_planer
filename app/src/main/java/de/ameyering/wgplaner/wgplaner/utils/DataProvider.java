package de.ameyering.wgplaner.wgplaner.utils;


import android.support.annotation.Nullable;

import java.util.ArrayList;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.*;
import io.swagger.client.model.User;

public abstract class DataProvider {

    public interface OnUpdatedDataListener{
        void onUpdatedData();
    }

    public static void initialize(String uid){
        Users.initialize(uid);
        CurrentGroup.initialize();
        ShoppingList.initialize();
    }

    public static abstract class Users {
        private static ArrayList<User> users;
        private static User currentUser = new User();

        private static ArrayList<OnUpdatedDataListener> mListeners;

        private static void initialize(String uid){
            users = new ArrayList<>();
            currentUser.setUid(uid);
            mListeners = new ArrayList<>();
        }

        public static void setCurrentUser(User user){
            if(user != null && !currentUser.equals(user)){
                currentUser = user;
                storeCurrentUser();
                callAllListeners();
            }
        }

        public static String getCurrentUsersUid(){
            if(currentUser.getUid() != null){
                return currentUser.getUid();
            }
            return "";
        }

        public static User getCurrentUser(){
            return currentUser;
        }

        public static int getUserCount(){
            return users.size() + 1;
        }

        public static boolean addUser(User user){
            if(user != null && !users.contains(user)){
                users.add(user);
                return true;
            }
            return false;
        }

        public static boolean removeUser(User user){
            if(user != null) {
                return users.remove(user);
            }
            return false;
        }

        @Nullable
        public static User getUserByUid(String uid){
            if(currentUser.getUid() != null && currentUser.getUid().equals(uid)){
                return currentUser;
            } else {
                for(User user: users){
                    if(user.getUid() != null && user.getUid().equals(uid)){
                        return user;
                    }
                }
                return null;
            }
        }

        public static ArrayList<User> getUsers(){
            ArrayList<User> users = (ArrayList<User>) Users.users.clone();
            users.add(currentUser);
            return users;
        }

        public static void initializeCurrentUser(@Nullable final ServerCalls.OnAsyncCallListener<User> listener){
            ServerCalls.getUserAsync(currentUser.getUid(), new ServerCalls.OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result) {
                    setCurrentUser(result);
                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static ApiResponse<User> initializeCurrentUser(){
            ApiResponse<User> response = ServerCalls.getUser(currentUser.getUid());
            if(response != null && response.getData() != null){
                setCurrentUser(response.getData());
            }
            return response;
        }

        public static void synchronizeCurrentUser(@Nullable final ServerCalls.OnAsyncCallListener<User> listener){
            ServerCalls.updateUserAsync(currentUser, new ServerCalls.OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result) {
                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static ApiResponse<User> synchronizeCurrentUser(){
            return ServerCalls.updateUser(currentUser);
        }

        public static void createCurrentUser(@Nullable final ServerCalls.OnAsyncCallListener<User> listener){
            ServerCalls.createUserAsync(currentUser, new ServerCalls.OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result) {
                    setCurrentUser(result);

                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        private static void storeCurrentUser(){
            if(currentUser.getUid() != null){
                Configuration.singleton.addConfig(Configuration.Type.USER_UID, currentUser.getUid());
            }
            if(currentUser.getDisplayName() != null){
                Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, currentUser.getDisplayName());
            }
            if(currentUser.getGroupUid() != null) {
                Configuration.singleton.addConfig(Configuration.Type.USER_GROUP_ID, currentUser.getGroupUid().toString());
            } else {
                Configuration.singleton.addConfig(Configuration.Type.USER_GROUP_ID, null);
            }
            Configuration.singleton.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, currentUser.getEmail());
            Configuration.singleton.addConfig(Configuration.Type.USER_PHOTO_URL, currentUser.getPhotoUrl());
            Configuration.singleton.addConfig(Configuration.Type.FIREBASE_INSTANCE_ID, currentUser.getFirebaseInstanceId());
        }

        public static boolean addOnUpdatedDataListener(OnUpdatedDataListener listener){
            if(listener != null && !mListeners.contains(listener)) {
                mListeners.add(listener);
                return true;
            }
            return false;
        }

        public static boolean removeOnUpdatedDataListener(OnUpdatedDataListener listener){
            if(listener != null){
                return mListeners.remove(listener);
            }
            return false;
        }

        private static void callAllListeners(){
            for(OnUpdatedDataListener listener: mListeners){
                listener.onUpdatedData();
            }
        }
    }

    public static abstract class CurrentGroup {
        private static Group group;

        private static ArrayList<OnUpdatedDataListener> mListeners;

        private static void initialize(){
            group = new Group();
            mListeners = new ArrayList<>();
        }

        public static void setGroup(Group group){
            if(group != null && !CurrentGroup.group.equals(group)){
                Users.currentUser.setGroupUid(group.getUid());
                CurrentGroup.group = group;
            }
        }

        public static Group getGroup(){
            return group;
        }

        public static void updateGroup(@Nullable final ServerCalls.OnAsyncCallListener<Group> listener){
            ServerCalls.getGroupAsync(Users.getCurrentUsersUid(), new ServerCalls.OnAsyncCallListener<Group>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result) {
                    initializeUsers();

                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static ApiResponse<Group> updateGroup(){
            ApiResponse<Group> response = ServerCalls.getGroup(Users.getCurrentUser().getGroupUid().toString());
            if(response != null && response.getData() != null){
                setGroup(response.getData());
            }
            return response;
        }

        public static void createGroup(Group group, @Nullable final ServerCalls.OnAsyncCallListener<Group> listener){
            ServerCalls.createGroupAsync(group, new ServerCalls.OnAsyncCallListener<Group>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result) {
                    if(listener != null){
                        setGroup(result);
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static ApiResponse<Group> createGroup(Group group){
            ApiResponse<Group> response = ServerCalls.createGroup(group);
            if(response != null && response.getData() != null){
                Group result = response.getData();
                setGroup(group);
                callAllListeners();
            }
            return response;
        }

        public static void joinGroup(String key, @Nullable final ServerCalls.OnAsyncCallListener<Group> listener){
            ServerCalls.joinGroupAsync(key, new ServerCalls.OnAsyncCallListener<Group>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result) {
                    if(group != null){
                        setGroup(result);
                        callAllListeners();

                        if(listener != null){
                            listener.onSuccess(result);
                        }
                    }
                }
            });
        }

        public static ApiResponse<Group> joinGroup(String key){
            ApiResponse<Group> response = ServerCalls.joinGroup(key);
            if(response != null && response.getData() != null){
                setGroup(response.getData());
            }
            return response;
        }

        public static void leaveGroup(@Nullable final ServerCalls.OnAsyncCallListener<SuccessResponse> listener){
            ServerCalls.leaveGroupAsync(new ServerCalls.OnAsyncCallListener<SuccessResponse>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(SuccessResponse result) {
                    group = new Group();
                    Users.currentUser.setGroupUid(null);

                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static ApiResponse<SuccessResponse> leaveGroup(){
            ApiResponse<SuccessResponse> response = ServerCalls.leaveGroup();
            if(response != null && response.getData() != null){
                group = new Group();
                Users.currentUser.setGroupUid(null);
            }
            return response;
        }

        private static void initializeUsers(){
            Users.users.clear();
            for(String uid: group.getMembers()){
                if(uid != null && !uid.isEmpty()){
                    ApiResponse<User> response = ServerCalls.getUser(uid);

                    if(response != null && response.getData() != null){
                        Users.users.add(response.getData());
                    }
                }
            }
        }

        public static boolean addOnUpdatedDataListener(OnUpdatedDataListener listener){
            if(listener != null && !mListeners.contains(listener)) {
                mListeners.add(listener);
                return true;
            }
            return false;
        }

        public static boolean removeOnUpdatedDataListener(OnUpdatedDataListener listener){
            if(listener != null){
                return mListeners.remove(listener);
            }
            return false;
        }

        private static void callAllListeners(){
            for(OnUpdatedDataListener listener: mListeners){
                listener.onUpdatedData();
            }
        }
    }

    public static abstract class ShoppingList {
        private static ArrayList<ListItem> shoppingList;
        private static ArrayList<ListItem> selectedItems;

        private static ArrayList<OnUpdatedDataListener> mListeners;

        private static void initialize(){
            shoppingList = new ArrayList<>();
            selectedItems = new ArrayList<>();
            mListeners = new ArrayList<>();
        }

        public static boolean selectItem(ListItem item){
            if(shoppingList.contains(item)){
                selectedItems.add(item);
                callAllListeners();
                return true;
            }
            return false;
        }

        public static ArrayList<ListItem> getShoppingList(){
            return shoppingList;
        }

        public static int getSelectedShoppingListCount(){
            return selectedItems.size();
        }

        public static boolean unselectItem(ListItem item){
            if(selectedItems.remove(item)){
                callAllListeners();
                return true;
            }
            return false;
        }

        public static boolean buySelection(){
            for(ListItem item: selectedItems){
                shoppingList.remove(item);
            }
            selectedItems.clear();
            callAllListeners();
            return true;
        }

        public static void addItem(ListItem item, @Nullable final ServerCalls.OnAsyncCallListener<ListItem> listener){
            ServerCalls.createShoppingListItemAsync(item, new ServerCalls.OnAsyncCallListener<ListItem>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(ListItem result) {
                    shoppingList.add(result);
                    callAllListeners();
                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static void updateShoppingList(@Nullable final ServerCalls.OnAsyncCallListener<io.swagger.client.model.ShoppingList> listener){
            ServerCalls.getShoppingListAsync(new ServerCalls.OnAsyncCallListener<io.swagger.client.model.ShoppingList>() {
                @Override
                public void onFailure(ApiException e) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(io.swagger.client.model.ShoppingList result) {
                    if(result != null){
                        if(result.getListItems() != null){
                            shoppingList = (ArrayList<ListItem>) result.getListItems();
                            callAllListeners();
                        } else {
                            shoppingList.clear();
                            callAllListeners();
                        }
                    }

                    if(listener != null){
                        listener.onSuccess(result);
                    }
                }
            });
        }

        public static ApiResponse<io.swagger.client.model.ShoppingList> updateShoppingList(){
            ApiResponse<io.swagger.client.model.ShoppingList> response = ServerCalls.getShoppingList();

            if(response != null && response.getData() != null){
                if(response.getData().getListItems() != null){
                    shoppingList = (ArrayList<ListItem>) response.getData().getListItems();
                    callAllListeners();
                } else {
                    shoppingList.clear();
                    callAllListeners();
                }
            }
            return response;
        }

        public static boolean addOnUpdatedDataListener(OnUpdatedDataListener listener){
            if(listener != null && !mListeners.contains(listener)) {
                mListeners.add(listener);
                return true;
            }
            return false;
        }

        public static boolean removeOnUpdatedDataListener(OnUpdatedDataListener listener){
            if(listener != null){
                return mListeners.remove(listener);
            }
            return false;
        }

        private static void callAllListeners(){
            for(OnUpdatedDataListener listener: mListeners){
                listener.onUpdatedData();
            }
        }
    }
}
