package de.ameyering.wgplaner.wgplaner.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

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
        CURRENT_USER, CURRENT_GROUP, SHOPPING_LIST, SELECTED_ITEMS
    }


    private static DataProvider singleton;

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

    private ArrayList<Bitmap> currentGroupMemberImages;

    private List<ListItem> currentShoppingList;
    private ArrayList<ListItem> selectedItems;

    private ArrayList<OnDataChangeListener> mListeners;

    static {
        singleton = new DataProvider();
    }

    public static void initialize(ServerCallsInterface serverCallsInstance) {
        singleton.setServerCallsInstance(serverCallsInstance);
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
        currentGroupMemberImages = null;
        currentGroupAccessKey = null;

        currentShoppingList = new ArrayList<>();
        selectedItems = new ArrayList<>();

        mListeners = new ArrayList<>();
    }

    private void setServerCallsInstance(ServerCallsInterface serverCallsInstance) {
        this.serverCallsInstance = serverCallsInstance;
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

                if(ImageStore.getInstance().getProfilePictureFile().length() < 5000L) {
                    ApiResponse<byte[]> imageResponse = serverCallsInstance.getUserImage(currentUserUid);

                    if (imageResponse != null && imageResponse.getStatusCode() == 200) {
                        currentUserPicture = BitmapFactory.decodeByteArray(imageResponse.getData(), 0,
                            imageResponse.getData().length);
                        ImageStore.getInstance().setProfilePicture(currentUserPicture);
                    }
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

                    if(ImageStore.getInstance().getProfilePictureFile().length() < 5000L){
                        ApiResponse<byte[]> imageResponse = serverCallsInstance.getGroupImage();

                        if(imageResponse != null && imageResponse.getData() != null){
                            ImageStore.getInstance().setGroupPicture(currentUserPicture = BitmapFactory.decodeByteArray(imageResponse.getData(), 0,
                                imageResponse.getData().length));
                        }
                    }

                    serverCallsInstance.getShoppingListAsync(new ServerCallsInterface.OnAsyncCallListener<ShoppingList>() {
                        @Override
                        public void onFailure(ApiException e) {
                            DataProvider.this.currentShoppingList = new ArrayList<>();
                            DataProvider.this.selectedItems = new ArrayList<>();
                        }

                        @Override
                        public void onSuccess(ShoppingList result) {
                            List<ListItem> items = result.getListItems();

                            if(items == null){
                                DataProvider.this.currentShoppingList = new ArrayList<>();
                                DataProvider.this.selectedItems = new ArrayList<>();
                            } else {
                                DataProvider.this.currentShoppingList = items;
                                DataProvider.this.selectedItems = new ArrayList<>();
                            }
                        }
                    });

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

                if (ImageStore.getInstance().getProfilePictureFile() != null) {
                    serverCallsInstance.updateUserImage(ImageStore.getInstance().getProfilePictureFile());
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void setFirebaseInstanceId(String token) {
        if (token != null) {
            this.currentUserFirebaseInstanceId = token;
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
            ImageStore.getInstance().setProfilePicture(bitmap);
            updateUserImage();
            callAllListeners(DataType.CURRENT_USER);
        }
    }

    private void updateUserImage() {
        File file = ImageStore.getInstance().getProfilePictureFile();

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
        if (currentUserPicture != null) {
            return currentUserPicture;
        }

        currentUserPicture = ImageStore.getInstance().getProfileBitmap(context);
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
            callAllListeners(DataType.CURRENT_GROUP);
        }
    }

    @Override
    public void setCurrentGroupCurrency(Currency currency) {
        if (currency != null) {
            this.currentGroupCurrency = currency;
            callAllListeners(DataType.CURRENT_GROUP);
        }
    }

    @Override
    public void setCurrentGroupImage(Bitmap bitmap) {
        ImageStore.getInstance().setGroupPicture(bitmap);
        serverCallsInstance.updateGroupImage(ImageStore.getInstance().getGroupPictureFile());
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
    public Bitmap getCurrentGroupImage() {
        return ImageStore.getInstance().getGroupBitmap();
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
        //TODO: Implement image flow

        ApiResponse<Group> groupResponse = createGroup(group);

        if (groupResponse != null && groupResponse.getData() != null) {
            group = groupResponse.getData();
            currentGroupUID = group.getUid();
            currentGroupName = group.getDisplayName();
            currentGroupCurrency = Currency.getInstance(group.getCurrency());
            currentGroupMembersUids = group.getMembers();
            currentGroupAdminsUids = group.getAdmins();
            initializeMembers(context);

            callAllListeners(DataType.CURRENT_GROUP);
            syncShoppingList();
            return true;
        }

        return false;
    }

    @Override
    public boolean joinCurrentGroup(String accessKey, Context context) {
        ApiResponse<Group> groupResponse = joinGroup(accessKey);

        if (groupResponse != null && groupResponse.getData() != null) {
            Group group = groupResponse.getData();
            currentGroupUID = group.getUid();
            currentGroupName = group.getDisplayName();
            currentGroupCurrency = Currency.getInstance(group.getCurrency());
            currentGroupMembersUids = group.getMembers();
            currentGroupAdminsUids = group.getAdmins();
            initializeMembers(context);

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
        if(currentGroupAccessKey == null){
            ApiResponse<GroupCode> codeResponse = serverCallsInstance.createGroupKey();

            if(codeResponse != null && codeResponse.getData() != null){
                String code = codeResponse.getData().getCode();
                return code;
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

                    if (itemResponse != null && itemResponse.getData() != null) {
                        return true;
                    }

                    return false;
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
            callAllListeners(DataType.SELECTED_ITEMS);
        }
    }

    @Override
    public void unselectShoppingListItem(ListItem item) {
        if (selectedItems.remove(item)) {
            callAllListeners(DataType.SELECTED_ITEMS);
        }
    }

    @Override
    public boolean isItemSelected(ListItem item) {
        return selectedItems.contains(item);
    }

    @Override
    public void buySelection() {
        ArrayList<UUID> items = new ArrayList<>();

        for(ListItem item: selectedItems){
            items.add(item.getId());
        }

        ApiResponse<SuccessResponse> buyResponse = serverCallsInstance.buyListItems(items);

        if(buyResponse != null && buyResponse.getData() != null){
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
        if (selectedItems != null) {
            return !selectedItems.isEmpty();

        } else {
            return false;
        }
    }

    @Override
    public void syncGroup(Context context) {
        ApiResponse<Group> groupResponse = getGroup();

        if (groupResponse != null && groupResponse.getData() != null) {
            Group group = groupResponse.getData();

            currentGroupUID = group.getUid();
            currentGroupName = group.getDisplayName();
            currentGroupCurrency = Currency.getInstance(group.getCurrency());
            currentGroupMembersUids = group.getMembers();
            currentGroupAdminsUids = group.getAdmins();
            initializeMembers(context);

            callAllListeners(DataType.CURRENT_GROUP);
            syncShoppingList();
        }
    }

    @Override
    public void syncShoppingList() {
        ApiResponse<ShoppingList> shoppingListResponse = getShoppingList();

        if (shoppingListResponse != null && shoppingListResponse.getData() != null) {
            List<ListItem> items = shoppingListResponse.getData().getListItems();

            if (items == null) {
                currentShoppingList = new ArrayList<>();
                selectedItems = new ArrayList<>();

            } else {
                currentShoppingList = items;

                if (selectedItems == null) {
                    selectedItems = new ArrayList<>();
                }

                for (ListItem item : selectedItems) {
                    if (!currentShoppingList.contains(item)) {
                        selectedItems.remove(item);
                    }
                }

                callAllListeners(DataType.SHOPPING_LIST);
                callAllListeners(DataType.SELECTED_ITEMS);
            }
        }
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
        group.setDisplayName(currentGroupName);
        group.setCurrency(currentGroupCurrency.getSymbol());
        group.setMembers(currentGroupMembersUids);
        group.setAdmins(currentGroupAdminsUids);

        //TODO: Implement Update group
        return null;
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

    private void initializeMembers(Context context) {
        currentGroupMembers = new ArrayList<>();
        currentGroupMemberImages = new ArrayList<>();

        for (String uid : currentGroupMembersUids) {
            if (uid != null) {
                ApiResponse<User> userResponse = getUser(uid);

                if (userResponse != null && userResponse.getData() != null) {
                    User user = userResponse.getData();
                    currentGroupMembers.add(user);

                    if (ImageStore.getInstance().loadGroupMemberPicture(user.getUid(), context) == null) {
                        ApiResponse<byte[]> imageResponse = serverCallsInstance.getUserImage(user.getUid());

                        if (imageResponse != null && imageResponse.getData() != null) {
                            ImageStore.getInstance().writeGroupMemberPicture(user.getUid(), imageResponse.getData(), context);
                        }
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
