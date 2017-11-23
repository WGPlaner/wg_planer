package de.ameyering.wgplaner.wgplaner.utils;


import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.swagger.client.*;
import io.swagger.client.Configuration;
import io.swagger.client.api.GroupApi;
import io.swagger.client.api.ShoppinglistApi;
import io.swagger.client.api.UserApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.model.*;
import io.swagger.client.model.User;

public abstract class ServerCalls {
    private static final String USER_ID_AUTH_LABEL = "UserIDAuth";
    private static final String FIREBASE_ID_AUTH_LABEL = "FirebaseIDAuth";

    private static final String BASE_URL = "https://api.wgplaner.ameyering.de";

    private static final String SERVER_CONNECTION_SUCCEEDED_TAG = "ServerConnectionSuccess";
    private static final String SERVER_CONNECTION_FAILED_TAG = "ServerConnectionFail";

    private static final String ASYNCHRONOUS_FLAG = ":Asynchronous";
    private static final String WAIT_FOR_RESULT_FLAG = ":WaitForResult";

    private static final String CREATE_USER_NAME = "CreateUser";
    private static final String UPDATE_USER_NAME = "UpdateUser";
    private static final String GET_USER_NAME = "GetUser";
    private static final String CREATE_GROUP_NAME = "CreateGroup";
    private static final String GET_GROUP_NAME = "GetGroup";
    private static final String JOIN_GROUP_NAME = "JoinGroup";
    private static final String GET_SHOPPING_LIST_NAME = "GetShoppingList";
    private static final String CREATE_SHOPPING_LIST_ITEM_NAME = "CreateShoppingListItem";

    public interface OnAsyncCallListener<T> {
        void onFailure(ApiException e);

        void onSuccess(T result);
    }

    private static ApiClient client;

    static {
        client = Configuration.getDefaultApiClient();
        client.setBasePath(BASE_URL);
    }

    private static boolean setAuth(String method){
        if(method != null && !method.isEmpty()){
            ApiKeyAuth auth = (ApiKeyAuth) client.getAuthentication(method);
            auth.setApiKey(DataProvider.Users.getCurrentUsersUid());
            return true;
        }
        return false;
    }

    public static void createUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener){
        setAuth(USER_ID_AUTH_LABEL);

        try {
            UserApi api = new UserApi();
            api.createUserAsync(user, new ApiCallback<User>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(CREATE_USER_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(CREATE_USER_NAME, ASYNCHRONOUS_FLAG);
                    if (result != null) {
                        DataProvider.Users.setCurrentUser(result);

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
                logError(CREATE_USER_NAME, ASYNCHRONOUS_FLAG);
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
                logError(CREATE_USER_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e) {
                logError(CREATE_USER_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void getUserAsync(String uid, @Nullable final OnAsyncCallListener<User> listener){
        setAuth(FIREBASE_ID_AUTH_LABEL);

        try{
            UserApi api = new UserApi();
            api.getUserAsync(uid, new ApiCallback<User>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(GET_USER_NAME, ASYNCHRONOUS_FLAG);
                    if(listener !=  null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(GET_USER_NAME, ASYNCHRONOUS_FLAG);
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
                logError(GET_USER_NAME, ASYNCHRONOUS_FLAG);
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
                logError(GET_USER_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e) {
                logError(GET_USER_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void updateUserAsync(User user, @Nullable final OnAsyncCallListener<User> listener){
        setAuth(USER_ID_AUTH_LABEL);

        try {
            UserApi api = new UserApi();
            api.updateUserAsync(user, new ApiCallback<User>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(UPDATE_USER_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(UPDATE_USER_NAME, ASYNCHRONOUS_FLAG);
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
                logError(UPDATE_USER_NAME, ASYNCHRONOUS_FLAG);
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
                logError(UPDATE_USER_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e){
                logError(UPDATE_USER_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void updateUserImageAsync(File image, @Nullable final OnAsyncCallListener<SuccessResponse> listener){
        if(image != null) {
            setAuth(USER_ID_AUTH_LABEL);

            try {
                UserApi api = new UserApi();
                api.updateUserImageAsync(DataProvider.Users.getCurrentUsersUid(), image, new ApiCallback<SuccessResponse>() {
                    @Override
                    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                        if(listener != null){
                            listener.onFailure(e);
                        }
                    }

                    @Override
                    public void onSuccess(SuccessResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
                        if(listener != null){
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
            } catch (ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }
        }
    }

    public static ApiResponse<SuccessResponse> updateUserImage(File image){
        if(image != null){
            UpdateUserImage task = new UpdateUserImage();
            try{
                return task.execute(image).get();
            } catch (ExecutionException e){
                return null;
            } catch (InterruptedException e){
                return null;
            }
        }
        return null;
    }

    public static void leaveGroupAsync(@Nullable final OnAsyncCallListener<SuccessResponse> listener){
        setAuth(USER_ID_AUTH_LABEL);

        try{
            GroupApi api = new GroupApi();
            api.leaveGroupAsync(new ApiCallback<SuccessResponse>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(SuccessResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
                    if(listener != null){
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

    public static ApiResponse<SuccessResponse> leaveGroup(){
        LeaveGroup task = new LeaveGroup();
        try{
            return task.execute().get();
        } catch (ExecutionException e){
            return null;
        } catch (InterruptedException e){
            return null;
        }
    }

    public static void getGroupAsync(String groupUid, @Nullable final OnAsyncCallListener<Group> listener){
        setAuth(USER_ID_AUTH_LABEL);

        try{
            GroupApi api = new GroupApi();
            api.getGroupAsync(groupUid, new ApiCallback<Group>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(GET_GROUP_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(GET_GROUP_NAME, ASYNCHRONOUS_FLAG);
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
            logError(GET_GROUP_NAME, ASYNCHRONOUS_FLAG);
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
                logError(GET_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e){
                logError(GET_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void createGroupAsync(Group group, @Nullable final OnAsyncCallListener<Group> listener){
        setAuth(USER_ID_AUTH_LABEL);

        try{
            GroupApi api = new GroupApi();
            api.createGroupAsync(group, new ApiCallback<Group>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(CREATE_GROUP_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(CREATE_GROUP_NAME, ASYNCHRONOUS_FLAG);
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
            logError(CREATE_GROUP_NAME, ASYNCHRONOUS_FLAG);
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
                logError(CREATE_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e){
                logError(CREATE_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void joinGroupAsync(String accessKey, @Nullable final OnAsyncCallListener<Group> listener){
        setAuth(USER_ID_AUTH_LABEL);

        try{
            GroupApi api = new GroupApi();
            api.joinGroupAsync(accessKey, new ApiCallback<Group>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(JOIN_GROUP_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(JOIN_GROUP_NAME, ASYNCHRONOUS_FLAG);
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
            logError(JOIN_GROUP_NAME, ASYNCHRONOUS_FLAG);
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
                logError(JOIN_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e){
                logError(JOIN_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void getShoppingListAsync(@Nullable final OnAsyncCallListener<ShoppingList> listener){
        setAuth(FIREBASE_ID_AUTH_LABEL);

        try {
            ShoppinglistApi api = new ShoppinglistApi();
            api.getListItemsAsync(DataProvider.Users.getCurrentUser().getGroupUid().toString(), new ApiCallback<ShoppingList>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(GET_SHOPPING_LIST_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(ShoppingList result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(GET_SHOPPING_LIST_NAME, ASYNCHRONOUS_FLAG);
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
            logError(GET_SHOPPING_LIST_NAME, ASYNCHRONOUS_FLAG);
            if(listener != null){
                listener.onFailure(e);
            }
        }
    }

    public static ApiResponse<ShoppingList> getShoppingList(){
        if(DataProvider.Users.getCurrentUser().getGroupUid() != null){
            GetShoppingList task = new GetShoppingList();
            try{
                return task.execute(DataProvider.Users.getCurrentUser().getGroupUid().toString()).get();
            } catch (ExecutionException e){
                logError(GET_SHOPPING_LIST_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e){
                logError(GET_SHOPPING_LIST_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    public static void createShoppingListItemAsync(ListItem item, @Nullable final OnAsyncCallListener<ListItem> listener){
        setAuth(FIREBASE_ID_AUTH_LABEL);

        try{
            ShoppinglistApi api = new ShoppinglistApi();
            api.createListItemAsync(DataProvider.Users.getCurrentUser().getGroupUid().toString(), item, new ApiCallback<ListItem>() {
                @Override
                public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    logError(CREATE_SHOPPING_LIST_ITEM_NAME, ASYNCHRONOUS_FLAG);
                    if(listener != null){
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(ListItem result, int statusCode, Map<String, List<String>> responseHeaders) {
                    logSuccess(CREATE_SHOPPING_LIST_ITEM_NAME, ASYNCHRONOUS_FLAG);
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
            logError(CREATE_SHOPPING_LIST_ITEM_NAME, ASYNCHRONOUS_FLAG);
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
                logError(CREATE_SHOPPING_LIST_ITEM_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            } catch (InterruptedException e){
                logError(CREATE_SHOPPING_LIST_ITEM_NAME, WAIT_FOR_RESULT_FLAG);
                return null;
            }
        }
        return null;
    }

    private static void logError(String name, String method){
        StringBuffer buffer = new StringBuffer();
        buffer.append(name).append(method);
        Log.e(SERVER_CONNECTION_FAILED_TAG, buffer.toString());
    }

    private static void logSuccess(String name, String method){
        StringBuffer buffer = new StringBuffer();
        buffer.append(name).append(method);
        Log.i(SERVER_CONNECTION_SUCCEEDED_TAG, buffer.toString());
    }

    private static class CreateUser extends AsyncTask<User, Void, ApiResponse<User>>{

        @Override
        protected ApiResponse<User> doInBackground(User... users) {
            if(users != null && users.length > 0) {
                try {
                    setAuth(USER_ID_AUTH_LABEL);
                    UserApi api = new UserApi();

                    return api.createUserWithHttpInfo(users[0]);
                } catch (ApiException e){
                    logError(CREATE_USER_NAME, WAIT_FOR_RESULT_FLAG);
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
                setAuth(FIREBASE_ID_AUTH_LABEL);
                UserApi api = new UserApi();

                try {
                    return api.getUserWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    logError(GET_USER_NAME, WAIT_FOR_RESULT_FLAG);
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
                setAuth(USER_ID_AUTH_LABEL);
                UserApi api = new UserApi();

                try {
                    return api.updateUserWithHttpInfo(users[0]);
                } catch (ApiException e){
                    logError(UPDATE_USER_NAME, WAIT_FOR_RESULT_FLAG);
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class UpdateUserImage extends AsyncTask<File, Void, ApiResponse<SuccessResponse>>{

        @Override
        protected ApiResponse<SuccessResponse> doInBackground(File... files) {
            if(files != null && files.length > 0 && files[0] != null){
                setAuth(USER_ID_AUTH_LABEL);
                UserApi api = new UserApi();

                try{
                    return api.updateUserImageWithHttpInfo(DataProvider.Users.getCurrentUsersUid(), files[0]);
                } catch (ApiException e){
                    return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }

    private static class LeaveGroup extends AsyncTask<Void, Void, ApiResponse<SuccessResponse>>{

        @Override
        protected ApiResponse<SuccessResponse> doInBackground(Void... voids) {
            setAuth(USER_ID_AUTH_LABEL);
            GroupApi api = new GroupApi();

            try{
                return api.leaveGroupWithHttpInfo();
            } catch (ApiException e){
                return new ApiResponse<>(e.getCode(), e.getResponseHeaders(), null);
            }
        }
    }

    private static class GetGroup extends AsyncTask<String, Void, ApiResponse<Group>>{

        @Override
        protected ApiResponse<Group> doInBackground(String... strings) {
            if(strings != null && strings.length > 0){
                setAuth(USER_ID_AUTH_LABEL);
                GroupApi api = new GroupApi();

                try{
                    return api.getGroupWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    logError(GET_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
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
                setAuth(USER_ID_AUTH_LABEL);
                GroupApi api = new GroupApi();

                try{
                    return api.createGroupWithHttpInfo(groups[0]);
                } catch (ApiException e){
                    logError(CREATE_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
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
                setAuth(USER_ID_AUTH_LABEL);
                GroupApi api = new GroupApi();

                try{
                    return api.joinGroupWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    logError(JOIN_GROUP_NAME, WAIT_FOR_RESULT_FLAG);
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
                setAuth(FIREBASE_ID_AUTH_LABEL);
                ShoppinglistApi api = new ShoppinglistApi();

                try{
                    return api.getListItemsWithHttpInfo(strings[0]);
                } catch (ApiException e){
                    logError(GET_SHOPPING_LIST_NAME, WAIT_FOR_RESULT_FLAG);
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
                setAuth(FIREBASE_ID_AUTH_LABEL);
                ShoppinglistApi api = new ShoppinglistApi();

                try {
                    return api.createListItemWithHttpInfo(DataProvider.Users.getCurrentUser().getGroupUid().toString(), listItems[0]);
                } catch (ApiException e){
                    logError(CREATE_SHOPPING_LIST_ITEM_NAME, WAIT_FOR_RESULT_FLAG);
                    return new ApiResponse<ListItem>(e.getCode(), e.getResponseHeaders(), null);
                }
            }
            return null;
        }
    }
}
