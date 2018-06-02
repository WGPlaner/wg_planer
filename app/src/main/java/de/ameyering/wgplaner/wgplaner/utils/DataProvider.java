package de.ameyering.wgplaner.wgplaner.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

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

public class DataProvider extends DataProviderInterface {
    private final ImageStore imageStoreInstance;
    private final ServerCallsInterface serverCallsInstance;

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

    private List<ListItem> boughtItems;

    private ArrayList<OnDataChangeListener> mListeners;

    public DataProvider(ServerCallsInterface serverCallsInterface, ImageStore imageStore) {
        imageStoreInstance = imageStore;
        serverCallsInstance = serverCallsInterface;

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

        boughtItems = new ArrayList<>();

        mListeners = new ArrayList<>();
    }

    public SetUpState initialize(String uid, Context context) {
        if (uid != null && !uid.trim().isEmpty()) {
            currentUserUid = uid;

            serverCallsInstance.setCurrentUserUid(currentUserUid);

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

                        updateUser(null);
                    }
                }

                if (imageStoreInstance.getProfilePictureFile().length() < 5000L) {
                    serverCallsInstance.getUserImageAsync(currentUserUid,
                    new OnAsyncCallListener<byte[]>() {
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
                ApiResponse<Group> groupResponse = serverCallsInstance.getGroup();

                if (groupResponse != null && groupResponse.getData() != null) {
                    Group group = groupResponse.getData();

                    currentGroupName = group.getDisplayName();
                    currentGroupCurrency = Currency.getInstance(group.getCurrency());
                    currentGroupMembersUids = group.getMembers();
                    currentGroupAdminsUids = group.getAdmins();
                    initializeMembers(context);

                    serverCallsInstance.getGroupImageAsync(new OnAsyncCallListener<byte[]>() {
                        @Override
                        public void onFailure(ApiException e) {
                            //TODO: Implement on failure
                        }

                        @Override
                        public void onSuccess(byte[] result) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                            imageStoreInstance.setGroupPicture(bitmap);
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
    public void registerUser(final OnAsyncCallListener<User> listener) {
        if (currentUserDisplayName != null && !currentUserDisplayName.isEmpty()) {
            User user = new User();
            user.setUid(currentUserUid);
            user.setDisplayName(currentUserDisplayName);
            user.setEmail(currentUserEmail);
            user.setFirebaseInstanceID(FirebaseInstanceId.getInstance().getToken());

            serverCallsInstance.createUserAsync(user, new OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result) {
                    currentUserUid = result.getUid();
                    currentUserDisplayName = result.getDisplayName();
                    currentUserEmail = result.getEmail();

                    if (currentUserFirebaseInstanceId.isEmpty()) {
                        currentUserFirebaseInstanceId = getFirebaseInstanceId();

                        if (!(currentUserFirebaseInstanceId != null && !currentUserFirebaseInstanceId.isEmpty())) {
                            currentUserFirebaseInstanceId = Configuration.singleton.getConfig(
                                    Configuration.Type.FIREBASE_INSTANCE_ID);

                            updateUser(null);
                        }
                    }

                    if (imageStoreInstance.getProfilePictureFile() != null) {
                        serverCallsInstance.updateUserImage(imageStoreInstance.getProfilePictureFile());
                    }

                    if (listener != null) {
                        listener.onSuccess(result);
                    }
                }
            });

        } else {
            if (listener != null) {
                listener.onFailure(null);
            }
        }
    }

    @Override
    public void setFirebaseInstanceId(String token, Context context) {
        if (token != null) {
            this.currentUserFirebaseInstanceId = token;

            if (Configuration.singleton == null) {
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
    public void setCurrentUserDisplayName(String displayName,
        final OnAsyncCallListener<User> listener) {
        if (displayName != null && !currentUserDisplayName.equals(displayName)) {
            this.currentUserDisplayName = displayName;
            Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);

            updateUser(new OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result) {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }

                    callAllListeners(DataType.CURRENT_USER);
                }
            });

        } else {
            if (listener != null) {
                listener.onFailure(null);
            }
        }
    }

    @Override
    public void setCurrentUserImage(Bitmap bitmap,
        final OnAsyncCallListener<SuccessResponse> listener) {
        if (bitmap != null) {
            currentUserPicture = bitmap;
            imageStoreInstance.setProfilePicture(bitmap);
            updateUserImage(new OnAsyncCallListener<SuccessResponse>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(SuccessResponse result) {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }

                    callAllListeners(DataType.CURRENT_USER);
                }
            });

        } else {
            if (listener != null) {
                listener.onFailure(null);
            }
        }
    }

    private void updateUserImage(final OnAsyncCallListener<SuccessResponse>
        listener) {
        File file = imageStoreInstance.getProfilePictureFile();

        serverCallsInstance.updateUserImageAsync(file,
        new OnAsyncCallListener<SuccessResponse>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(SuccessResponse result) {
                callAllListeners(DataType.CURRENT_USER);

                if (listener != null) {
                    listener.onSuccess(result);
                }
            }
        });
    }

    @Override
    public void setCurrentUserEmail(@Nullable String email,
        final OnAsyncCallListener<User> listener) {
        this.currentUserEmail = email;
        Configuration.singleton.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, email);

        updateUser(new OnAsyncCallListener<User>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(User result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }

                callAllListeners(DataType.CURRENT_USER);
            }
        });
    }

    @Override
    public void setCurrentUserLocale(Locale locale,
        final OnAsyncCallListener<User> listener) {
        if (locale != null && !currentUserLocale.equals(locale)) {
            this.currentUserLocale = locale;
            updateUser(new OnAsyncCallListener<User>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(User result) {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }

                    callAllListeners(DataType.CURRENT_USER);
                }
            });

        } else {
            if (listener != null) {
                listener.onFailure(null);
            }
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
    public void setCurrentGroupName(String groupName,
        final OnAsyncCallListener<Group> listener) {
        if (groupName != null && !groupName.isEmpty()) {
            this.currentGroupName = groupName;

            updateGroup(new OnAsyncCallListener<Group>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result) {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }

                    callAllListeners(DataType.CURRENT_GROUP);
                }
            });

        } else {
            if (listener != null) {
                listener.onFailure(null);
            }
        }
    }

    @Override
    public void setCurrentGroupCurrency(Currency currency,
        final OnAsyncCallListener<Group> listener) {
        if (currency != null) {
            this.currentGroupCurrency = currency;

            updateGroup(new OnAsyncCallListener<Group>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(Group result) {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }

                    callAllListeners(DataType.CURRENT_GROUP);
                }
            });
        }
    }

    @Override
    public void setCurrentGroupImage(Bitmap bitmap,
        final OnAsyncCallListener<SuccessResponse> listener) {
        imageStoreInstance.setGroupPicture(bitmap);

        serverCallsInstance.updateGroupImageAsync(imageStoreInstance.getGroupPictureFile(),
        new OnAsyncCallListener<SuccessResponse>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(SuccessResponse result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }

                callAllListeners(DataType.CURRENT_GROUP);
            }
        });
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
    public void updateGroup(Group group, OnAsyncCallListener<Group> listener) {
        serverCallsInstance.updateGroupAsync(group, new OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(Group result) {
                currentGroupName = result.getDisplayName();
                currentGroupCurrency = Currency.getInstance(result.getCurrency());
                currentGroupUID = result.getUid();

                if (listener != null) {
                    listener.onSuccess(result);
                }

                callAllListeners(DataType.CURRENT_GROUP);
            }
        });
    }

    @Override
    public void createGroup(String name, String groupCurrency, Bitmap imagescr, Context context,
        final OnAsyncCallListener<Group> listener) {
        Group group = new Group();
        group.setDisplayName(name);
        group.setCurrency(groupCurrency);
        imageStoreInstance.setGroupPicture(imagescr);

        createGroup(group, new OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(Group group) {
                currentGroupUID = group.getUid();
                currentGroupName = group.getDisplayName();
                currentGroupCurrency = Currency.getInstance(group.getCurrency());
                currentGroupMembersUids = group.getMembers();
                currentGroupAdminsUids = group.getAdmins();
                imageStoreInstance.setGroupPicture(imagescr);
                serverCallsInstance.updateGroupImageAsync(imageStoreInstance.getGroupPictureFile(), null);

                serverCallsInstance.updateGroupImage(imageStoreInstance.getGroupPictureFile());

                initializeMembers(context);

                if (listener != null) {
                    listener.onSuccess(group);
                }

                callAllListeners(DataType.CURRENT_GROUP);
                syncShoppingList();

            }
        });
    }

    @Override
    public void joinCurrentGroup(String accessKey, final Context context,
        final OnAsyncCallListener<Group> listener) {
        joinGroup(accessKey, new OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(Group group) {
                currentGroupUID = group.getUid();
                currentGroupName = group.getDisplayName();
                currentGroupCurrency = Currency.getInstance(group.getCurrency());
                currentGroupMembersUids = group.getMembers();
                currentGroupAdminsUids = group.getAdmins();
                initializeMembers(context);

                serverCallsInstance.getGroupImageAsync(new OnAsyncCallListener<byte[]>() {
                    @Override
                    public void onFailure(ApiException e) {
                        Log.e("GroupImage", ":GetFailure");
                    }

                    @Override
                    public void onSuccess(byte[] result) {
                        ImageStore.getInstance().setGroupPicture(BitmapFactory.decodeByteArray(result, 0, result.length));
                    }
                });

                if (listener != null) {
                    listener.onSuccess(group);
                }

                callAllListeners(DataType.CURRENT_GROUP);
                syncShoppingList();
            }
        });
    }

    @Override
    public void leaveCurrentGroup(final OnAsyncCallListener<SuccessResponse>
        listener) {
        leaveGroup(new OnAsyncCallListener<SuccessResponse>() {
            @Override
            public void onFailure(ApiException e) {
                //Nothing happens
            }

            @Override
            public void onSuccess(SuccessResponse result) {
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
            }
        });
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
    public void addShoppingListItem(ListItem item,
        final OnAsyncCallListener<ListItem> listener) {
        if (item != null && currentShoppingList != null) {
            boolean wasUpdated = false;

            for (ListItem currentItem : currentShoppingList) {
                if (currentItem.getTitle().equals(item.getTitle()) &&
                    currentItem.getRequestedFor().equals(item.getRequestedFor())) {
                    int pos = currentShoppingList.indexOf(currentItem);

                    currentItem.setCount(currentItem.getCount() + item.getCount());

                    updateListItem(item, new OnAsyncCallListener<ListItem>() {
                        @Override
                        public void onFailure(ApiException e) {
                            if (listener != null) {
                                listener.onFailure(e);
                            }
                        }

                        @Override
                        public void onSuccess(ListItem result) {
                            if (listener != null) {
                                listener.onSuccess(result);

                                currentShoppingList.remove(pos);
                                currentShoppingList.add(result);
                                callAllListeners(DataType.SHOPPING_LIST);
                            }
                        }
                    });

                    wasUpdated = true;
                    break;
                }
            }

            if (!wasUpdated) {
                addListItem(item, new OnAsyncCallListener<ListItem>() {
                    @Override
                    public void onFailure(ApiException e) {
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    }

                    @Override
                    public void onSuccess(ListItem result) {
                        if (listener != null) {
                            listener.onSuccess(result);

                            currentShoppingList.add(result);
                            callAllListeners(DataType.SHOPPING_LIST);
                        }
                    }
                });
            }

        } else {
            if (listener != null) {
                listener.onFailure(null);
            }
        }
    }

    @Override
    public void selectShoppingListItem(ListItem item) {
        if (item != null && currentShoppingList != null && selectedItems != null &&
            currentShoppingList.contains(item)) {
            selectedItems.add(item);

            if (selectedItems.size() == 1) {
                callAllListeners(DataType.SELECTED_ITEMS);
            }
        }
    }

    @Override
    public void unselectShoppingListItem(ListItem item) {
        if (selectedItems.remove(item) && selectedItems.size() == 0) {
            callAllListeners(DataType.SELECTED_ITEMS);
        }
    }

    @Override
    public boolean isItemSelected(ListItem item) {
        return selectedItems.contains(item);
    }

    @Override
    public void buySelection(final OnAsyncCallListener<SuccessResponse> listener) {
        ArrayList<UUID> items = new ArrayList<>();

        for (ListItem item : selectedItems) {
            items.add(item.getId());
        }

        serverCallsInstance.buyListItemsAsync(items,
        new OnAsyncCallListener<SuccessResponse>() {
            @Override
            public void onFailure(ApiException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
            }

            @Override
            public void onSuccess(SuccessResponse result) {
                if (listener != null) {
                    listener.onSuccess(result);
                }

                selectedItems.clear();
                syncShoppingList();
                callAllListeners(DataType.SELECTED_ITEMS);
            }
        });
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
    public void addPriceToListItem(ListItem item, String price,
        final OnAsyncCallListener<ListItem> listener) {
        try {
            Double priceDouble = Double.parseDouble(price) * 100;
            Integer priceInt = priceDouble.intValue();

            item.setPrice(priceInt);

            updateListItem(item, new OnAsyncCallListener<ListItem>() {
                @Override
                public void onFailure(ApiException e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }

                @Override
                public void onSuccess(ListItem result) {
                    if (listener != null) {
                        listener.onSuccess(result);
                    }

                    syncShoppingList();
                    syncBoughtItems();
                }
            });

        } catch (NullPointerException | NumberFormatException e) {
            if (listener != null) {
                listener.onFailure(null);
            }
        }
    }

    @Override
    public boolean isSomethingSelected() {
        return selectedItems != null && !selectedItems.isEmpty();
    }

    @Override
    public ArrayList<ListItem> getBoughtItems() {
        if (boughtItems != null) {
            ArrayList<ListItem> items = new ArrayList<>();
            items.addAll(boughtItems);
            return items;
        }

        return new ArrayList<>();
    }

    @Override
    public void syncBoughtItems() {
        if (currentUserUid != null) {
            ApiResponse<ShoppingList> boughtItemsResponse = serverCallsInstance.getBoughtItems(currentUserUid);

            if (boughtItemsResponse != null && boughtItemsResponse.getData() != null) {
                ShoppingList list = boughtItemsResponse.getData();

                boughtItems.clear();
                boughtItems.addAll(list.getListItems());

                callAllListeners(DataType.BOUGHT_ITEMS);
            }
        }
    }

    @Override
    public void syncGroup() {
        getGroup(new OnAsyncCallListener<Group>() {
            @Override
            public void onFailure(ApiException e) {
                //Nothing happens
            }

            @Override
            public void onSuccess(Group group) {
                currentGroupUID = group.getUid();
                currentGroupName = group.getDisplayName();
                currentGroupCurrency = Currency.getInstance(group.getCurrency());
                currentGroupMembersUids = group.getMembers();
                currentGroupAdminsUids = group.getAdmins();

                callAllListeners(DataType.CURRENT_GROUP);
            }
        });

        serverCallsInstance.getGroupImageAsync(new OnAsyncCallListener<byte[]>() {
            @Override
            public void onFailure(ApiException e) {
                //Do nothing
            }

            @Override
            public void onSuccess(byte[] result) {
                imageStoreInstance.writeGroupPicture(result);

                callAllListeners(DataType.CURRENT_GROUP);
            }
        });
    }

    @Override
    public ListItem getListItem(UUID uuid) {
        for (ListItem item : currentShoppingList) {
            if (item.getId().equals(uuid)) {
                return item;
            }
        }

        for (ListItem item : boughtItems) {
            if (item.getId().equals(uuid)) {
                return item;
            }
        }

        return null;
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

                if (selectedItemRemoved && selectedItems.size() == 0) {
                    callAllListeners(DataType.SELECTED_ITEMS);
                }

            } else {
                currentShoppingList = new ArrayList<>();
                callAllListeners(DataType.SHOPPING_LIST);

                if (selectedItems.size() > 0) {
                    selectedItems.clear();
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
            imageStoreInstance.writeGroupPicture(imageResponse.getData());

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

    private void updateUser(final OnAsyncCallListener<User> listener) {
        User user = new User();
        user.setUid(currentUserUid);
        user.setDisplayName(currentUserDisplayName);
        user.setFirebaseInstanceID(FirebaseInstanceId.getInstance().getToken());
        user.setEmail(currentUserEmail);
        user.setGroupUID(currentGroupUID);

        serverCallsInstance.updateUserAsync(user, listener);
    }

    private void getUser(String uid, final OnAsyncCallListener<User> listener) {
        serverCallsInstance.getUserAsync(uid, listener);
    }

    private void getGroup(final OnAsyncCallListener<Group> listener) {
        serverCallsInstance.getGroupAsync(listener);
    }

    private void updateGroup(final OnAsyncCallListener<Group> listener) {
        Group group = new Group();
        group.setDisplayName(currentGroupName);
        group.setCurrency(currentGroupCurrency.getCurrencyCode());
        group.setMembers(currentGroupMembersUids);
        group.setAdmins(currentGroupAdminsUids);

        serverCallsInstance.updateGroupAsync(group, listener);
    }

    private void joinGroup(String accessKey,
        final OnAsyncCallListener<Group> listener) {
        serverCallsInstance.joinGroupAsync(accessKey, listener);
    }

    private void leaveGroup(final OnAsyncCallListener<SuccessResponse> listener) {
        serverCallsInstance.leaveGroupAsync(listener);
    }

    private void createGroup(Group group,
        final OnAsyncCallListener<Group> listener) {
        serverCallsInstance.createGroupAsync(group, listener);
    }

    /*
    private void getShoppingList(final ServerCallsInterface.OnAsyncCallListener<ShoppingList>
        listener) {
        serverCallsInstance.getShoppingListAsync(listener);
    }
    */

    private void addListItem(ListItem item,
        final OnAsyncCallListener<ListItem> listener) {
        serverCallsInstance.createShoppingListItemAsync(item, listener);
    }

    private void updateListItem(ListItem item,
        final OnAsyncCallListener<ListItem> listener) {
        serverCallsInstance.updateShoppingListItemAsync(item, listener);
    }

    private void initializeMembers(final Context context) {
        currentGroupMembers = new ArrayList<>();

        for (String uid : currentGroupMembersUids) {
            if (uid != null) {
                getUser(uid, new OnAsyncCallListener<User>() {
                    @Override
                    public void onFailure(ApiException e) {
                        //Nothing happens
                    }

                    @Override
                    public void onSuccess(User user) {
                        currentGroupMembers.add(user);

                        if (imageStoreInstance.loadGroupMemberPicture(user.getUid(), context) == null) {
                            serverCallsInstance.getUserImageAsync(user.getUid(),
                            new OnAsyncCallListener<byte[]>() {
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
                });
            }
        }
    }

    private void callAllListeners(DataType type) {
        for (OnDataChangeListener listener : mListeners) {
            listener.onDataChanged(type);
        }
    }

    @Override
    public void addOnDataChangeListener(OnDataChangeListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeOnDataChangeListener(OnDataChangeListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }
}
