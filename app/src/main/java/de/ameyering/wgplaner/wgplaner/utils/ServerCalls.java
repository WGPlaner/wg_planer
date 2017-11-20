package de.ameyering.wgplaner.wgplaner.utils;


import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.swagger.client.*;
import io.swagger.client.api.GroupApi;
import io.swagger.client.api.ShoppinglistApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.model.*;

public abstract class ServerCalls {
    private static final String USER_ID_AUTH_LABEL = "UserIDAuth";
    private static final String FIREBASE_ID_AUTH_LABEL = "FirebaseIDAuth";

    private static final String BASE_URL = "https://api.wgplaner.ameyering.de/v0.1";

    public interface OnAsyncCallListener<T> {
        void onFailure(ApiException e);

        void onSuccess(T result);
    }

    public static void createUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener){
        UserApi api = new UserApi();

        ApiClient client = api.getApiClient();

        ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
        firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

        client.setBasePath(BASE_URL);

        try {
            api.createUserAsync(user, new ApiCallback<User>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if (result != null) {
                        DataContainer.Me.setMe(result);

                        if(listener != null){
                            listener.onSuccess(result);
                        }
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<User> createUser(User user){
        if(user != null) {
            CreateUser task = new CreateUser();
            try {
                return task.execute(user).get();
            } catch (ExecutionException e) {
                return null;
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null;
    }

    public static void getUserAsync(String uid, @Nullable final OnAsyncCallListener<User> listener){
        ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

        client.setBasePath(BASE_URL);

        ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(FIREBASE_ID_AUTH_LABEL);
        firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

        UserApi api = new UserApi();

        try{
            api.getUserAsync(uid, new ApiCallback<User>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener !=  null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener != null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<User> getUser(String uid){
        if(uid != null) {
            GetUser task = new GetUser();
            try {
                return task.execute(uid).get();
            } catch (ExecutionException e) {
                return null;
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null;
    }

    public static void updateUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener){
        ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

        client.setBasePath(BASE_URL);

        ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
        firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

        UserApi api = new UserApi();

        try {
            api.updateUserAsync(user, new ApiCallback<User>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener !=  null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<User> updateUser(User user){
        if(user != null){
            UpdateUser task = new UpdateUser();
            try{
                return task.execute(user).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    public static void getGroupAsync(String groupUid, @Nullable final OnAsyncCallListener<Group> listener){
        GroupApi api = new GroupApi();

        ApiClient client = api.getApiClient();

        ApiKeyAuth userAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
        userAuth.setApiKey(DataContainer.Me.getMe().getUid());

        client.setBasePath(BASE_URL);

        try{
            api.getGroupAsync(groupUid, new ApiCallback<Group>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener != null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<Group> getGroup(String groupUid){
        if(groupUid != null){
            GetGroup task = new GetGroup();
            try{
                return task.execute(groupUid).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    public static void createGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener){
        GroupApi api = new GroupApi();

        ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

        ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
        firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

        client.setBasePath(BASE_URL);

        try{
            api.createGroupAsync(group, new ApiCallback<Group>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener != null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch(ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<Group> createGroup(Group group){
        if(group != null) {
            CreateGroup task = new CreateGroup();
            try {
                return task.execute(group).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    public static void joinGroupAsync(String accessKey, @Nullable final OnAsyncCallListener<Group> listener){
        ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

        client.setBasePath(BASE_URL);

        ApiKeyAuth UserIDAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
        UserIDAuth.setApiKey(DataContainer.Me.getMe().getUid());

        GroupApi api = new GroupApi();

        try{
            api.joinGroupAsync(accessKey, new ApiCallback<Group>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener != null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<Group> joinGroup(String accessKey){
        if(accessKey != null){
            JoinGroup task = new JoinGroup();
            try{
                return task.execute(accessKey).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    public static void getShoppingListAsync(@Nullable final OnAsyncCallListener<ShoppingList> listener){
        ShoppinglistApi api = new ShoppinglistApi();

        ApiClient client = api.getApiClient();

        ApiKeyAuth userAuth = (ApiKeyAuth) client.getAuthentication(FIREBASE_ID_AUTH_LABEL);
        userAuth.setApiKey(DataContainer.Me.getMe().getUid());

        client.setBasePath(BASE_URL);

        try {
            api.getListItemsAsync(DataContainer.Me.getMe().getGroupUid().toString(), new ApiCallback<ShoppingList>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(ShoppingList result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener != null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<ShoppingList> getShoppingList(){
        if(DataContainer.Me.getMe().getGroupUid() != null){
            GetShoppingList task = new GetShoppingList();
            try{
                return task.execute(DataContainer.Me.getMe().getGroupUid().toString()).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    public static void createShoppingListItemAsync(ListItem item, @Nullable final OnAsyncCallListener<ListItem> listener){
        ShoppinglistApi api = new ShoppinglistApi();

        ApiClient client = api.getApiClient();

        ApiKeyAuth userAuth = (ApiKeyAuth) client.getAuthentication(FIREBASE_ID_AUTH_LABEL);
        userAuth.setApiKey(DataContainer.Me.getMe().getUid());

        client.setBasePath(BASE_URL);

        try{
            api.createListItemAsync(DataContainer.Me.getMe().getGroupUid().toString(), item, new ApiCallback<ListItem>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(ListItem result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(result != null && listener != null){
                        listener.onSuccess(result);
                    }
                }

                @Override
                public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {

                }

                @Override
                public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {

                }
            });
        } catch (ApiException e){
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<ListItem> createShoppingListItem(ListItem item){
        if(item != null){
            CreateShoppingListItem task = new CreateShoppingListItem();
            try{
                return task.execute(item).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    private static class CreateUser extends AsyncTask<User, Void, ApiResponse<User>>{

        @Override
        protected ApiResponse<User> doInBackground(User... users) {
            if(users != null && users.length > 0) {
                try {
                    UserApi api = new UserApi();

                    ApiClient client = api.getApiClient();

                    ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
                    firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

                    client.setBasePath(BASE_URL);

                    return api.createUserWithHttpInfo(users[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class GetUser extends AsyncTask<String, Void, ApiResponse<User>>{

        @Override
        protected ApiResponse<User> doInBackground(String... strings) {
            if(strings != null && strings.length > 0){
                ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

                client.setBasePath(BASE_URL);

                ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(FIREBASE_ID_AUTH_LABEL);
                firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

                UserApi api = new UserApi();

                try {
                    return api.getUserWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class UpdateUser extends AsyncTask<User, Void, ApiResponse<User>>{

        @Override
        protected ApiResponse<User> doInBackground(User... users) {
            if(users != null && users.length > 0){
                ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

                client.setBasePath(BASE_URL);

                ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
                firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

                UserApi api = new UserApi();

                try {
                    return api.updateUserWithHttpInfo(users[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class GetGroup extends AsyncTask<String, Void, ApiResponse<Group>>{

        @Override
        protected ApiResponse<Group> doInBackground(String... strings) {
            if(strings != null && strings.length > 0){
                GroupApi api = new GroupApi();

                ApiClient client = api.getApiClient();

                ApiKeyAuth userAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
                userAuth.setApiKey(DataContainer.Me.getMe().getUid());

                client.setBasePath(BASE_URL);

                try{
                    return api.getGroupWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class CreateGroup extends AsyncTask<Group, Void, ApiResponse<Group>>{

        @Override
        protected ApiResponse<Group> doInBackground(Group... groups) {
            if(groups != null && groups.length > 0){
                GroupApi api = new GroupApi();

                ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

                ApiKeyAuth firebaseAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
                firebaseAuth.setApiKey(DataContainer.Me.getMe().getUid());

                client.setBasePath(BASE_URL);

                try{
                    return api.createGroupWithHttpInfo(groups[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class JoinGroup extends AsyncTask<String, Void, ApiResponse<Group>>{

        @Override
        protected ApiResponse<Group> doInBackground(String... strings) {
            if(strings != null && strings.length > 0){
                ApiClient client = io.swagger.client.Configuration.getDefaultApiClient();

                client.setBasePath(BASE_URL);

                ApiKeyAuth UserIDAuth = (ApiKeyAuth) client.getAuthentication(USER_ID_AUTH_LABEL);
                UserIDAuth.setApiKey(DataContainer.Me.getMe().getUid());

                GroupApi api = new GroupApi();

                try{
                    return api.joinGroupWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class GetShoppingList extends AsyncTask<String, Void, ApiResponse<ShoppingList>>{

        @Override
        protected ApiResponse<ShoppingList> doInBackground(String... strings) {
            if(strings != null && strings.length > 0){
                ShoppinglistApi api = new ShoppinglistApi();

                ApiClient client = api.getApiClient();

                ApiKeyAuth userAuth = (ApiKeyAuth) client.getAuthentication(FIREBASE_ID_AUTH_LABEL);
                userAuth.setApiKey(DataContainer.Me.getMe().getUid());

                client.setBasePath(BASE_URL);
                try{
                    return api.getListItemsWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class CreateShoppingListItem extends AsyncTask<ListItem, Void, ApiResponse<ListItem>>{

        @Override
        protected ApiResponse<ListItem> doInBackground(ListItem... listItems) {
            if(listItems != null && listItems.length > 0){
                ShoppinglistApi api = new ShoppinglistApi();

                ApiClient client = api.getApiClient();

                ApiKeyAuth userAuth = (ApiKeyAuth) client.getAuthentication(FIREBASE_ID_AUTH_LABEL);
                userAuth.setApiKey(DataContainer.Me.getMe().getUid());

                client.setBasePath(BASE_URL);

                try {
                    return api.createListItemWithHttpInfo(DataContainer.Me.getMe().getGroupUid().toString(), listItems[0]);
                } catch (ApiException e){
                    return new ApiResponse<ListItem>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }
}
