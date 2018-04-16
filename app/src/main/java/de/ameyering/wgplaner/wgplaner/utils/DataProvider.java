package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.GroupCode;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;

public class DataProvider implements DataProviderInterface {

    public interface OnDataChangeListener {
        void onDataChanged(final DataType type);
    }

    public enum DataType {
        CURRENT_USER, CURRENT_GROUP, SHOPPING_LIST, SELECTED_ITEMS, CURRENT_GROUP_MEMBERS
    }

    private static DataProvider singleton;

    private ImageStore imageStoreInstance = ImageStore.getInstance();
    private ServerCallsInterface serverCallsInstance;

    private String currentUserUid;
    private String currentUserFirebaseInstanceId;
    private String currentUserDisplayName;
    private String currentUserEmail;
    private Locale currentUserLocale;
    private Bitmap currentUserPicture;

    private UUID currentGroupUID;
    private String currentGroupName;
    private Currency currentGroupCurrency;
    private List<String> currentGroupMembersUids;
    private List<String> currentGroupAdminsUids;
    private ArrayList<User> currentGroupMembers;

    private String currentGroupAccessKey;

    private List<ListItem> currentShoppingList;
    private ArrayList<ListItem> selectedItems;

    private ArrayList<OnDataChangeListener> mListeners;

    static {
        singleton = new DataProvider();
        singleton.setServerCallsInstance(ServerCalls.getInstance());
    }
  
    public void setServerCallsInstance(ServerCallsInterface serverCallsInstance) {
        this.serverCallsInstance = serverCallsInstance;
    }

    public static boolean isInitialized() {
        return singleton.serverCallsInstance != null;
    }

    public static DataProvider getInstance() {
        return singleton;
    }

    private DataProvider() {
        currentUserUid = "";
        currentUserDisplayName = "";
        currentUserEmail = null;
        currentUserFirebaseInstanceId = "";
        currentUserLocale = null;

        currentGroupUID = null;
        currentGroupName = "";
        currentGroupCurrency = null;
        currentGroupMembersUids = null;
        currentGroupAdminsUids = null;
        currentGroupMembers = null;
        currentGroupAccessKey = null;

        currentShoppingList = new ArrayList<>();
        selectedItems = new ArrayList<>();

        mListeners = new ArrayList<>();
    }

    public SetUpState initialize(String uid, Context context) {
        if (uid != null && !uid.isEmpty()) {
            currentUserUid = uid;
            Configuration.singleton.addConfig(Configuration.Type.USER_UID, uid);
            ApiResponse<User> userResponse = serverCallsInstance.getUser(uid);

            if (userResponse != null && userResponse.getData() != null) {
                User user = userResponse.getData();

                currentUserUid = user.getUid();
                currentUserDisplayName = user.getDisplayName();
                currentUserEmail = user.getEmail();
                currentGroupUID = user.getGroupUID();

                if (currentUserFirebaseInstanceId.isEmpty()) {
                    currentUserFirebaseInstanceId = user.getFirebaseInstanceID();

                    if (!(currentUserFirebaseInstanceId != null && !currentUserFirebaseInstanceId.isEmpty())) {
                        currentUserFirebaseInstanceId = Configuration.singleton.getConfig(
                                Configuration.Type.FIREBASE_INSTANCE_ID);

                        updateUser();
                    }
                }

                if (imageStoreInstance.getProfilePictureFile().length() < 5000L) {
                    serverCallsInstance.getUserImageAsync(currentUserUid,
                    new ServerCallsInterface.OnAsyncCallListener<byte[]>() {
                        @Override
                        public void onFailure(ApiException e) {
                            //TODO: Implement failure
                        }

                        @Override
                        public void onSuccess(byte[] result) {
                            currentUserPicture = BitmapFactory.decodeByteArray(result, 0,
                                    result.length);
                            imageStoreInstance.setProfilePicture(currentUserPicture);

                        }
                    });
                }

            } else if (userResponse != null && userResponse.getData() == null) {
                if (userResponse.getStatusCode() == 404) {
                    return SetUpState.UNREGISTERED;

                } else if (userResponse.getStatusCode() != 0) {
                    return SetUpState.GET_USER_FAILED;

                } else {
                    return SetUpState.CONNECTION_FAILED;
                }

            } else {
                return SetUpState.CONNECTION_FAILED;
            }

            if (currentGroupUID != null) {
                ApiResponse<Group> groupResponse = serverCallsInstance.getGroup(currentGroupUID);

                if (groupResponse != null && groupResponse.getData() != null) {
                    Group group = groupResponse.getData();

                    currentGroupName = group.getDisplayName();
                    currentGroupCurrency = Currency.getInstance(group.getCurrency());
                    currentGroupMembersUids = group.getMembers();
                    currentGroupAdminsUids = group.getAdmins();
                    initializeMembers(context);

                    serverCallsInstance.getGroupImageAsync(new ServerCallsInterface.OnAsyncCallListener<byte[]>() {
                        @Override
                        public void onFailure(ApiException e) {
                            //TODO: Implement on failure
                        }

                        @Override
                        public void onSuccess(byte[] result) {
                            currentUserPicture = BitmapFactory.decodeByteArray(result, 0, result.length);
                            imageStoreInstance.setGroupPicture(currentUserPicture);
                        }
                    });

                    /*
                    serverCallsInstance.getShoppingListAsync(new
                    ServerCallsInterface.OnAsyncCallListener<ShoppingList>() {
                        @Override
                        public void onFailure(ApiException e) {
                            DataProvider.this.currentShoppingList = new ArrayList<>();
                            DataProvider.this.selectedItems = new ArrayList<>();
                        }

                        @Override
                        public void onSuccess(ShoppingList result) {
                            List<ListItem> items = result.getListItems();

                            if (items == null) {
                                DataProvider.this.currentShoppingList = new ArrayList<>();
                                DataProvider.this.selectedItems = new ArrayList<>();
                                callAllListeners(DataType.SHOPPING_LIST);
                                callAllListeners(DataType.SELECTED_ITEMS);

                            } else {
                                DataProvider.this.currentShoppingList = items;
                                DataProvider.this.selectedItems = new ArrayList<>();
                                callAllListeners(DataType.SHOPPING_LIST);
                                callAllListeners(DataType.SELECTED_ITEMS);
                            }
                        }
                    });
                    */

                    return SetUpState.SETUP_COMPLETED;

                } else {
                    return SetUpState.GET_GROUP_FAILED;
                }

            } else {
                return SetUpState.REGISTERED;
            }
        }

        return SetUpState.GET_USER_FAILED;
    }

    @Override
    public boolean registerUser() {
        if (currentUserDisplayName != null && !currentUserDisplayName.isEmpty()) {
            User user = new User();
            user.setUid(currentUserUid);
            user.setDisplayName(currentUserDisplayName);
            user.setEmail(currentUserEmail);
            user.setFirebaseInstanceID(currentUserFirebaseInstanceId);

            ApiResponse<User> userResponse = serverCallsInstance.createUser(user);

            if (userResponse != null && userResponse.getData() != null) {
                user = userResponse.getData();
                currentUserUid = user.getUid();
                currentUserDisplayName = user.getDisplayName();
                currentUserEmail = user.getEmail();

                if (currentUserFirebaseInstanceId.isEmpty()) {
                    currentUserFirebaseInstanceId = getFirebaseInstanceId();

                    if (!(currentUserFirebaseInstanceId != null && !currentUserFirebaseInstanceId.isEmpty())) {
                        currentUserFirebaseInstanceId = Configuration.singleton.getConfig(
                                Configuration.Type.FIREBASE_INSTANCE_ID);

                        updateUser();
                    }
                }

                if (imageStoreInstance.getProfilePictureFile() != null) {
                    serverCallsInstance.updateUserImage(imageStoreInstance.getProfilePictureFile());
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void setFirebaseInstanceId(String token, Context context) {
        if (token != null) {
            this.currentUserFirebaseInstanceId = token;
            if(Configuration.singleton == null) {
                Configuration.initConfig(context);
            }
            Configuration.singleton.addConfig(Configuration.Type.FIREBASE_INSTANCE_ID, token);
        }
    }

    @Override
    public String getFirebaseInstanceId() {
        return currentUserFirebaseInstanceId;
    }

    @Override
    public void setCurrentUserDisplayName(String displayName) {
        if (displayName != null && !currentUserDisplayName.equals(displayName)) {
            this.currentUserDisplayName = displayName;
            Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);
            updateUser();
            callAllListeners(DataType.CURRENT_USER);
        }
    }

    @Override
    public void setCurrentUserImage(Bitmap bitmap) {
        if (bitmap != null) {
            currentUserPicture = bitmap;
            imageStoreInstance.setProfilePicture(bitmap);
            updateUserImage();
            callAllListeners(DataType.CURRENT_USER);
        }
    }

    private void updateUserImage() {
        File file = imageStoreInstance.getProfilePictureFile();

        if (file != null) {
            ApiResponse<SuccessResponse> imageResponse = serverCallsInstance.updateUserImage(file);

            if (imageResponse != null && imageResponse.getData() != null) {
                callAllListeners(DataType.CURRENT_USER);
            }
        }
    }

    @Override
    public void setCurrentUserEmail(@Nullable String email) {
        this.currentUserEmail = email;
        Configuration.singleton.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, email);
        updateUser();
        callAllListeners(DataType.CURRENT_USER);
    }

    @Override
    public void setCurrentUserLocale(Locale locale) {
        if (locale != null && !currentUserLocale.equals(locale)) {
            this.currentUserLocale = locale;
            updateUser();
            callAllListeners(DataType.CURRENT_USER);
        }
    }

    @Override
    public String getCurrentUserUid() {
        return currentUserUid;
    }

    @Override
    public String getCurrentUserDisplayName() {
        return currentUserDisplayName;
    }

    @Override
    public Bitmap getCurrentUserImage(Context context) {
        currentUserPicture = imageStoreInstance.getProfileBitmap(context);
        return currentUserPicture;
    }

    @Override
    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    @Override
    public Locale getCurrentUserLocale() {
        return currentUserLocale;
    }

    @Override
    public void setCurrentGroupName(String groupName) {
        if (groupName != null && !groupName.isEmpty()) {
            this.currentGroupName = groupName;

            ApiResponse<Group> groupResponse = updateGroup();

            if (groupResponse != null && groupResponse.getData() != null) {
                callAllListeners(DataType.CURRENT_GROUP);
            }
        }
    }

    @Override
    public void setCurrentGroupCurrency(Currency currency) {
        if (currency != null) {
            this.currentGroupCurrency = currency;

            ApiResponse<Group> groupResponse = updateGroup();

            if (groupResponse != null && groupResponse.getData() != null) {
                callAllListeners(DataType.CURRENT_GROUP);
            }
        }
    }

    @Override
    public void setCurrentGroupImage(Bitmap bitmap) {
        imageStoreInstance.setGroupPicture(bitmap);
        serverCallsInstance.updateGroupImage(imageStoreInstance.getGroupPictureFile());
    }

    @Override
    public UUID getCurrentGroupUID() {
        return currentGroupUID;
    }

    @Override
    public String getCurrentGroupName() {
        return currentGroupName;
    }

    @Override
    public Currency getCurrentGroupCurrency() {
        return currentGroupCurrency;
    }

    @Override
    public Bitmap getCurrentGroupImage(Context context) {
        return imageStoreInstance.getGroupBitmap(context);
    }

    @Override
    public ArrayList<User> getCurrentGroupMembers() {
        return currentGroupMembers;
    }

    @Override
    public User getUserByUid(String uid) {
        if (currentGroupMembers != null) {
            for (User user : currentGroupMembers) {
                if (user.getUid().equals(uid)) {
                    return user;
                }
            }
        }

        return new User();
    }

    @Override
    public boolean isAdmin(String uid) {
        return currentGroupAdminsUids.contains(uid);
    }

    @Override
    public boolean createGroup(String name, Currency currency, Bitmap imagecr, Context context) {
        Group group = new Group();
        group.setDisplayName(name);
        group.setCurrency(currency.getCurrencyCode());
        imageStoreInstance.setGroupPicture(imagecr);

        ApiResponse<Group> groupResponse = createGroup(group);

        if (groupResponse != null && groupResponse.getData() != null) {
            group = groupResponse.getData();
            currentGroupUID = group.getUid();
            currentGroupName = group.getDisplayName();
            currentGroupCurrency = Currency.getInstance(group.getCurrency());
            currentGroupMembersUids = group.getMembers();
            currentGroupAdminsUids = group.getAdmins();
            imageStoreInstance.setGroupPicture(imagecr);
            serverCallsInstance.updateGroupImageAsync(imageStoreInstance.getGroupPictureFile(), null);

            ApiResponse<SuccessResponse> imageResponse = serverCallsInstance.updateGroupImage(imageStoreInstance.getGroupPictureFile());

            initializeMembers(context);

            callAllListeners(DataType.CURRENT_GROUP);
            syncShoppingList();
            return true;
        }

        return false;
    }

    @Override
    public boolean joinCurrentGroup(String accessKey, final Context context) {
        ApiResponse<Group> groupResponse = joinGroup(accessKey);

        if (groupResponse != null && groupResponse.getData() != null) {
            Group group = groupResponse.getData();
            currentGroupUID = group.getUid();
            currentGroupName = group.getDisplayName();
            currentGroupCurrency = Currency.getInstance(group.getCurrency());
            currentGroupMembersUids = group.getMembers();
            currentGroupAdminsUids = group.getAdmins();
            initializeMembers(context);

            serverCallsInstance.getGroupImageAsync(new ServerCallsInterface.OnAsyncCallListener<byte[]>() {
                @Override
                public void onFailure(ApiException e) {
                    Log.e("GroupImage", ":GetFailure");
                }

                @Override
                public void onSuccess(byte[] result) {
                    ImageStore.getInstance().setGroupPicture(BitmapFactory.decodeByteArray(result, 0, result.length));
                }
            });

            callAllListeners(DataType.CURRENT_GROUP);
            syncShoppingList();
            return true;
        }

        return false;
    }

    @Override
    public boolean leaveCurrentGroup() {
        ApiResponse<SuccessResponse> groupResponse = leaveGroup();

        if (groupResponse != null && groupResponse.getData() != null) {
            currentGroupUID = null;
            currentGroupName = null;
            currentGroupCurrency = null;
            currentGroupMembers = null;
            currentGroupAdminsUids = null;
            currentGroupMembersUids = null;
            currentShoppingList = new ArrayList<>();
            selectedItems = new ArrayList<>();

            callAllListeners(DataType.CURRENT_GROUP);
            callAllListeners(DataType.SHOPPING_LIST);
            callAllListeners(DataType.SELECTED_ITEMS);

            return true;
        }

        return false;
    }

    @Override
    public String createGroupAccessKey() {
        if (currentGroupAccessKey == null) {
            ApiResponse<GroupCode> codeResponse = serverCallsInstance.createGroupKey();

            if (codeResponse != null && codeResponse.getData() != null) {
                return codeResponse.getData().getCode();

            } else {
                return null;
            }

        } else {
            return currentGroupAccessKey;
        }
    }

    @Override
    public boolean addShoppingListItem(ListItem item) {
        if (item != null && currentShoppingList != null) {
            for (ListItem currentItem : currentShoppingList) {
                if (currentItem.getTitle().equals(item.getTitle()) &&
                    currentItem.getRequestedFor().equals(item.getRequestedFor())) {
                    currentItem.setCount(currentItem.getCount() + item.getCount());
                    ApiResponse<ListItem> itemResponse = updateListItem(currentItem);

                    return itemResponse != null && itemResponse.getData() != null;

                }
            }

            ApiResponse<ListItem> itemRepsonse = addListItem(item);

            if (itemRepsonse != null && itemRepsonse.getData() != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void selectShoppingListItem(ListItem item) {
        if (item != null && currentShoppingList != null && selectedItems != null &&
            currentShoppingList.contains(item)) {
            selectedItems.add(item);
            if(selectedItems.size() == 1) {
                callAllListeners(DataType.SELECTED_ITEMS);
            }
        }
    }

    @Override
    public void unselectShoppingListItem(ListItem item) {
        if (selectedItems.remove(item)) {
            if(selectedItems.size() == 0) {
                callAllListeners(DataType.SELECTED_ITEMS);
            }
        }
    }

    @Override
    public boolean isItemSelected(ListItem item) {
        return selectedItems.contains(item);
    }

    @Override
    public void buySelection() {
        ArrayList<UUID> items = new ArrayList<>();

        for (ListItem item : selectedItems) {
            items.add(item.getId());
        }

        ApiResponse<SuccessResponse> buyResponse = serverCallsInstance.buyListItems(items);

        if (buyResponse != null && buyResponse.getData() != null) {
            selectedItems.clear();
            callAllListeners(DataType.SELECTED_ITEMS);
        }
    }

    @Override
    public ArrayList<ListItem> getCurrentShoppingList() {
        if (currentShoppingList != null) {
            ArrayList<ListItem> items = new ArrayList<>();
            items.addAll(currentShoppingList);
            return items;
        }

        return new ArrayList<>();
    }

    @Override
    public boolean isSomethingSelected() {
        return selectedItems != null && !selectedItems.isEmpty();
    }

    @Override
    public void syncGroup() {
        ApiResponse<Group> groupResponse = getGroup();

        if (groupResponse != null && groupResponse.getData() != null) {
            Group group = groupResponse.getData();

            currentGroupUID = group.getUid();
            currentGroupName = group.getDisplayName();
            currentGroupCurrency = Currency.getInstance(group.getCurrency());
            currentGroupMembersUids = group.getMembers();
            currentGroupAdminsUids = group.getAdmins();

            callAllListeners(DataType.CURRENT_GROUP);
        }
    }

    @Override
    public void syncGroupNewMember(String uid, Context context) {
        if (!currentGroupMembersUids.contains(uid)) {
            currentGroupMembersUids.add(uid);

            ApiResponse<User> userResponse = serverCallsInstance.getUser(uid);

            if (userResponse != null && userResponse.getData() != null) {
                currentGroupMembers.add(userResponse.getData());

                ApiResponse<byte[]> imageResponse = serverCallsInstance.getUserImage(uid);

                if (imageResponse != null && imageResponse.getData() != null) {
                    imageStoreInstance.writeGroupMemberPicture(uid, imageResponse.getData(), context);
                }

                callAllListeners(DataType.CURRENT_GROUP_MEMBERS);
                syncShoppingList();

            } else {
                currentGroupMembersUids.remove(uid);
            }
        }
    }

    @Override
    public void syncGroupMemberPicture(String uid, Context context) {
        if (currentGroupMembersUids.contains(uid)) {
            ApiResponse<byte[]> imageResponse = serverCallsInstance.getUserImage(uid);

            if (imageResponse != null && imageResponse.getData() != null) {
                imageStoreInstance.writeGroupMemberPicture(uid, imageResponse.getData(), context);
            }
        }
    }

    @Override
    public ApiResponse<ShoppingList> syncShoppingList() {
        ApiResponse<ShoppingList> listResponse = serverCallsInstance.getShoppingList();

        if (listResponse != null && listResponse.getData() != null) {
            List<ListItem> items = listResponse.getData().getListItems();

            if (items != null) {
                currentShoppingList = items;
                boolean selectedItemRemoved = false;

                for (ListItem item : selectedItems) {
                    if (!currentShoppingList.contains(item)) {
                        selectedItems.remove(item);
                        selectedItemRemoved = true;
                    }
                }

                callAllListeners(DataType.SHOPPING_LIST);
                if(selectedItemRemoved && selectedItems.size() == 0) {
                    callAllListeners(DataType.SELECTED_ITEMS);
                }

            } else {
                currentShoppingList = new ArrayList<>();
                callAllListeners(DataType.SHOPPING_LIST);

                if(selectedItems.size() > 0) {
                    selectedItems = new ArrayList<>();
                    callAllListeners(DataType.SELECTED_ITEMS);
                }
            }
        }

        return listResponse;
    }

    @Override
    public void syncGroupPicture(Context context) {
        ApiResponse<byte[]> imageResponse = serverCallsInstance.getGroupImage();

        if (imageResponse != null && imageResponse.getData() != null) {
            imageStoreInstance.setGroupPicture(BitmapFactory.decodeByteArray(imageResponse.getData(), 0,
                    imageResponse.getData().length));

            callAllListeners(DataType.CURRENT_GROUP);
        }
    }

    @Override
    public void syncGroupMember(String uid) {
        int pos = currentGroupMembersUids.indexOf(uid);

        if (pos != -1) {
            ApiResponse<User> userResponse = serverCallsInstance.getUser(uid);

            if (userResponse != null && userResponse.getData() != null) {
                currentGroupMembers.remove(pos);
                currentGroupMembers.add(pos, userResponse.getData());
                callAllListeners(DataType.CURRENT_GROUP_MEMBERS);
            }
        }
    }

    @Override
    public void syncGroupMemberLeft(String uid, Context context) {
        int pos = currentGroupMembersUids.indexOf(uid);

        if (pos != -1) {
            currentGroupMembersUids.remove(pos);
            currentGroupMembers.remove(pos);
            imageStoreInstance.deleteGroupMemberPicture(uid, context);
            callAllListeners(DataType.CURRENT_GROUP_MEMBERS);
        }
    }

    @Override
    public void syncGroupMembers() {
        //currently nothing implemented
    }

    private ApiResponse<User> updateUser() {
        User user = new User();
        user.setUid(currentUserUid);
        user.setDisplayName(currentUserDisplayName);
        user.setFirebaseInstanceID(currentUserFirebaseInstanceId);
        user.setEmail(currentUserEmail);
        user.setGroupUID(currentGroupUID);

        return serverCallsInstance.updateUser(user);
    }

    private ApiResponse<User> getUser(String uid) {
        return serverCallsInstance.getUser(uid);
    }

    private ApiResponse<Group> getGroup() {
        return serverCallsInstance.getGroup(currentGroupUID);
    }

    private ApiResponse<Group> updateGroup() {
        Group group = new Group();
        group.setUid(currentGroupUID);
        group.setDisplayName(currentGroupName);
        group.setCurrency(currentGroupCurrency.getCurrencyCode());
        group.setMembers(currentGroupMembersUids);
        group.setAdmins(currentGroupAdminsUids);

        return serverCallsInstance.updateGroup(group);
    }

    private ApiResponse<Group> joinGroup(String accessKey) {
        return serverCallsInstance.joinGroup(accessKey);
    }

    private ApiResponse<SuccessResponse> leaveGroup() {
        return serverCallsInstance.leaveGroup();
    }

    private ApiResponse<Group> createGroup(Group group) {
        return serverCallsInstance.createGroup(group);
    }

    private ApiResponse<ShoppingList> getShoppingList() {
        return serverCallsInstance.getShoppingList();
    }

    private ApiResponse<ListItem> addListItem(ListItem item) {
        return serverCallsInstance.createShoppingListItem(item);
    }

    private ApiResponse<ListItem> updateListItem(ListItem item) {
        return serverCallsInstance.updateShoppingListItem(item);
    }

    private void initializeMembers(final Context context) {
        currentGroupMembers = new ArrayList<>();

        for (String uid : currentGroupMembersUids) {
            if (uid != null) {
                ApiResponse<User> userResponse = getUser(uid);

                if (userResponse != null && userResponse.getData() != null) {
                    final User user = userResponse.getData();
                    currentGroupMembers.add(user);

                    if (imageStoreInstance.loadGroupMemberPicture(user.getUid(), context) == null) {
                        serverCallsInstance.getUserImageAsync(user.getUid(),
                        new ServerCallsInterface.OnAsyncCallListener<byte[]>() {
                            @Override
                            public void onFailure(ApiException e) {
                                //nothing happens so far...
                            }

                            @Override
                            public void onSuccess(byte[] result) {
                                imageStoreInstance.writeGroupMemberPicture(user.getUid(), result, context);
                            }
                        });
                    }
                }
            }
        }
    }

    private void callAllListeners(DataType type) {
        for (OnDataChangeListener listener : mListeners) {
            listener.onDataChanged(type);
        }
    }

    public void addOnDataChangeListener(OnDataChangeListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    public void removeOnDataChangeListener(OnDataChangeListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }
}
