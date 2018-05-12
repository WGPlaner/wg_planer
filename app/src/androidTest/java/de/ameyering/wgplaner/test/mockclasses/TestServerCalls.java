package de.ameyering.wgplaner.test.mockclasses;

import android.support.annotation.Nullable;
import android.util.ArrayMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.ameyering.wgplaner.wgplaner.utils.ServerCallsInterface;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.Group;
import io.swagger.client.model.GroupCode;
import io.swagger.client.model.ListItem;
import io.swagger.client.model.ShoppingList;
import io.swagger.client.model.SuccessResponse;
import io.swagger.client.model.User;


public class TestServerCalls implements ServerCallsInterface {
    private static TestServerCalls singleton;
    private static final String DEFAULT_DISPLAY_NAME = "Arne";
    private static final String DEFAULT_EMAIL = "arne@schulze.de";

    private static final String DEFAULT_GROUP_NAME = "Arne's Group";
    private static final String DEFAULT_CURRENCY = "EUR";
    private static final List<String> DEFAULT_GROUP_MEMBERS;
    private static final List<String> DEFAULT_GROUP_ADMINS;
    private static final List<ListItem> DEFAULT_LIST_ITEMS;


    private TestCase testCase;

    static {
        singleton = new TestServerCalls();
        DEFAULT_GROUP_MEMBERS = new ArrayList<>();
        DEFAULT_GROUP_ADMINS = new ArrayList<>();
        DEFAULT_LIST_ITEMS = new ArrayList<>();

        List<String> uids = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < (random.nextInt(10) + 1); i++) {
            uids.add(random.nextInt(3000) + "UID");
        }

        DEFAULT_GROUP_MEMBERS.addAll(uids);
        DEFAULT_GROUP_ADMINS.addAll(uids);
    }

    public static TestServerCalls getInstance() {
        return singleton;
    }

    public static TestServerCalls getInstance(TestCase testCase) {
        singleton.testCase = testCase;
        return singleton;
    }

    private TestServerCalls() {
        testCase = TestCase.SUCCESS;
    }

    public enum TestCase {
        SUCCESS,
        FAILURE
    }

    @Override
    public void createUserAsync(User user, @Nullable OnAsyncCallListener<User> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(user);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<User> createUser(User user) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<User>(200, new ArrayMap<String, List<String>>(), user);
            }

            case FAILURE: {
                return new ApiResponse<User>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void getUserAsync(String uid, @Nullable OnAsyncCallListener<User> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    User result = new User();
                    result.setUid(uid);
                    result.setDisplayName(DEFAULT_DISPLAY_NAME);
                    result.setEmail(DEFAULT_EMAIL);
                    listener.onSuccess(result);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<User> getUser(String uid) {
        switch (testCase) {
            case SUCCESS: {
                User result = new User();
                result.setUid(uid);
                result.setDisplayName(DEFAULT_DISPLAY_NAME);
                result.setEmail(DEFAULT_EMAIL);
                return new ApiResponse<User>(200, null, result);
            }

            case FAILURE: {
                return new ApiResponse<User>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void updateUserAsync(User user, @Nullable OnAsyncCallListener<User> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(user);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<User> updateUser(User user) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<User>(200, null, user);
            }

            case FAILURE: {
                return new ApiResponse<User>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void getUserImageAsync(String uid, @Nullable OnAsyncCallListener<byte[]> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    byte[] bytes = new byte[5000];
                    new Random().nextBytes(bytes);

                    listener.onSuccess(bytes);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<byte[]> getUserImage(String uid) {
        switch (testCase) {
            case SUCCESS: {
                byte[] bytes = new byte[5000];
                new Random().nextBytes(bytes);

                return new ApiResponse<byte[]>(200, null, bytes);
            }

            case FAILURE: {
                return new ApiResponse<byte[]>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void updateUserImageAsync(File image,
        @Nullable OnAsyncCallListener<SuccessResponse> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(new SuccessResponse());
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<SuccessResponse> updateUserImage(File image) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<>(200, null, new SuccessResponse());
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void leaveGroupAsync(@Nullable OnAsyncCallListener<SuccessResponse> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(new SuccessResponse());
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<SuccessResponse> leaveGroup() {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<>(200, null, new SuccessResponse());
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void getGroupAsync(UUID groupUid, @Nullable OnAsyncCallListener<Group> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    Group group = new Group();
                    group.setCurrency(DEFAULT_CURRENCY);
                    group.setDisplayName(DEFAULT_GROUP_NAME);
                    group.setMembers(DEFAULT_GROUP_MEMBERS);
                    group.setAdmins(DEFAULT_GROUP_ADMINS);


                    listener.onSuccess(group);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<Group> getGroup(UUID groupUid) {
        switch (testCase) {
            case SUCCESS: {
                Group group = new Group();
                group.setCurrency(DEFAULT_CURRENCY);
                group.setDisplayName(DEFAULT_GROUP_NAME);
                group.setMembers(DEFAULT_GROUP_MEMBERS);
                group.setAdmins(DEFAULT_GROUP_ADMINS);

                return new ApiResponse<>(200, null, group);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void updateGroupAsync(Group group, @Nullable OnAsyncCallListener<Group> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(group);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException());
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<Group> updateGroup(Group group) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<Group>(200, null, group);
            }

            case FAILURE: {
                return new ApiResponse<Group>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void createGroupAsync(Group group, @Nullable OnAsyncCallListener<Group> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(group);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<Group> createGroup(Group group) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<>(200, null, group);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void joinGroupAsync(String accessKey, @Nullable OnAsyncCallListener<Group> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    Group group = new Group();
                    group.setCurrency(DEFAULT_CURRENCY);
                    group.setDisplayName(DEFAULT_GROUP_NAME);
                    group.setMembers(DEFAULT_GROUP_MEMBERS);
                    group.setAdmins(DEFAULT_GROUP_ADMINS);


                    listener.onSuccess(group);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<Group> joinGroup(String accessKey) {
        switch (testCase) {
            case SUCCESS: {
                Group group = new Group();
                group.setUid(new UUID(11111111111111L, 000000000001L));
                group.setCurrency(DEFAULT_CURRENCY);
                group.setDisplayName(DEFAULT_GROUP_NAME);
                group.setMembers(DEFAULT_GROUP_MEMBERS);
                group.setAdmins(DEFAULT_GROUP_ADMINS);

                return new ApiResponse<>(200, null, group);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void createGroupKeyAsync(@Nullable OnAsyncCallListener<GroupCode> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    GroupCode code = new GroupCode();
                    code.setCode("MYSUPERDUPER");
                    listener.onSuccess(code);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<GroupCode> createGroupKey() {
        switch (testCase) {
            case SUCCESS: {
                GroupCode code = new GroupCode();
                code.setCode("MYSUPERDUPER");

                return new ApiResponse<>(200, null, (GroupCode) code);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void getGroupImageAsync(@Nullable OnAsyncCallListener<byte[]> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    byte[] bytes = new byte[5000];
                    new Random().nextBytes(bytes);

                    listener.onSuccess(bytes);
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<byte[]> getGroupImage() {
        switch (testCase) {
            case SUCCESS: {
                byte[] bytes = new byte[5000];
                new Random().nextBytes(bytes);

                return new ApiResponse<byte[]>(200, null, bytes);
            }

            case FAILURE: {
                return new ApiResponse<byte[]>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void updateGroupImageAsync(File image,
        @Nullable OnAsyncCallListener<SuccessResponse> listener) {
        if (listener != null) {
            switch (testCase) {
                case SUCCESS: {
                    listener.onSuccess(new SuccessResponse());
                }
                break;

                case FAILURE: {
                    listener.onFailure(new ApiException(0, "Failure"));
                }
                break;
            }
        }
    }

    @Override
    public ApiResponse<SuccessResponse> updateGroupImage(File image) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<>(200, null, new SuccessResponse());
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void getShoppingListAsync(@Nullable OnAsyncCallListener<ShoppingList> listener) {
        switch (testCase) {
            case SUCCESS: {
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setListItems(DEFAULT_LIST_ITEMS);
                listener.onSuccess(shoppingList);
            }
            break;

            case FAILURE: {
                listener.onFailure(new ApiException(0, "Failure"));
            }
            break;
        }
    }

    @Override
    public ApiResponse<ShoppingList> getShoppingList() {
        switch (testCase) {
            case SUCCESS: {
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setListItems(DEFAULT_LIST_ITEMS);
                return new ApiResponse<>(200, null, shoppingList);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void createShoppingListItemAsync(ListItem item,
        @Nullable OnAsyncCallListener<ListItem> listener) {
        switch (testCase) {
            case SUCCESS: {
                listener.onSuccess(item);
            }
            break;

            case FAILURE: {
                listener.onFailure(new ApiException(0, "Failure"));
            }
            break;
        }
    }

    @Override
    public ApiResponse<ListItem> createShoppingListItem(ListItem item) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<>(200, null, item);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void updateShoppingListItemAsync(ListItem item,
        @Nullable OnAsyncCallListener<ListItem> listener) {
        switch (testCase) {
            case SUCCESS: {
                listener.onSuccess(item);
            }
            break;

            case FAILURE: {
                listener.onFailure(new ApiException(0, "Failure"));
            }
            break;
        }
    }

    @Override
    public ApiResponse<ListItem> updateShoppingListItem(ListItem item) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<>(200, null, item);
            }

            case FAILURE: {
                return new ApiResponse<>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }

    @Override
    public void buyListItemsAsync(List<UUID> items,
        @Nullable OnAsyncCallListener<SuccessResponse> listener) {
        switch (testCase) {
            case SUCCESS: {
                listener.onSuccess(new SuccessResponse());
            }
            break;

            case FAILURE: {
                listener.onFailure(new ApiException());
            }
            break;
        }
    }

    @Override
    public ApiResponse<SuccessResponse> buyListItems(List<UUID> items) {
        switch (testCase) {
            case SUCCESS: {
                return new ApiResponse<SuccessResponse>(200, null, new SuccessResponse());
            }

            case FAILURE: {
                return new ApiResponse<SuccessResponse>(0, null, null);
            }

            default: {
                return null;
            }
        }
    }
}
