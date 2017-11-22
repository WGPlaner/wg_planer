package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.model.*;
import io.swagger.client.model.User;

public abstract class DataContainer {

    public interface OnDataChangeListener {
        void onDataChange();
    }

    public enum CallBehavior {
        WAIT_FOR_RETURN, PARALLEL
    }

    public static void initialize(){
        Me.initialize();
        Groups.initialize();
        GroupMembers.initialize();
        Users.initialize();
        ShoppingListItems.initialize();
        SelectedShoppingListItems.initialize();
    }

    /**
     * DataContainer for ShoppingListItems
     * <p>
     *     Contains data for the Shopping-List Section
     *     All Update/Create/Delete calls should be done through its methods
     * </p>
     */

    public static abstract class ShoppingListItems {
        private static ArrayList<ListItem> shoppingListItems;

        private static ArrayList<OnDataChangeListener> mListeners;

        private static void initialize(){
            shoppingListItems = new ArrayList<>();
            mListeners = new ArrayList<>();

            Groups.addOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    if(Groups.group.getUid() != null){
                        updateShoppingList(CallBehavior.PARALLEL);
                    }
                }
            });
        }

        /**
         * @return Returns the current ShoppingList (can not be null)
         */

        public static ArrayList<ListItem> getShoppingListItems() {
            ArrayList<ListItem> items = new ArrayList<>();
            items.addAll(shoppingListItems);
            return items;
        }

        /**
         * This method removes all active ShoppingList items and adds a ShoppingList instance to the current ShoppingList
         * <b>This method clears the ShoppingList</b>
         * <p/>
         *
         * @param list An Instance of ShoppingList (can be null)
         */

        public static void addAllShoppingListItems(ShoppingList list){
            shoppingListItems.clear();

            List<ListItem> shoppingList = list.getListItems();

            if(shoppingList != null){
                shoppingListItems.addAll(shoppingList);
            }

            callAllListeners();
        }

        private static int contains(ListItem item) {
            for (int i = 0; i < shoppingListItems.size(); i++) {
                ListItem listItem = shoppingListItems.get(i);

                if (
                    listItem.getTitle() != null && item.getTitle() != null &&
                    listItem.getRequestedFor() != null && item.getRequestedFor() != null) {
                    if (listItem.getTitle().equals(item.getTitle()) &&
                        listItem.getRequestedFor().equals(item.getRequestedFor())) {
                        return i;
                    }
                }
            }

            return -1;
        }

        /**
         * Removes an Item from the ShoppingList
         * <p/>
         * @param item An Instance of ListItem which will be removed from the ShoppingList
         *             if the ShoppingList contains this item
         */

        public static void removeShoppingListItem(ListItem item) {
            int pos = contains(item);

            if (pos != -1) {
                shoppingListItems.remove(pos);
                callAllListeners();
            }
        }

        /**
         * Method to add an OnDataChangeListener to the ShoppingList
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 ShoppingList Data changes
         */

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        /**
         * Method to remove an OnDataChangeListener from the ShoppingList
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 ShoppingList Data changes
         */

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

        /**
         * Method to remove all OnDataChangeListeners currently attached to the ShoppingList
         */

        public static void removeAllOnDataChangeListeners(){
            mListeners.clear();
        }

        private static void callAllListeners(){
            for(OnDataChangeListener listener: mListeners){
                listener.onDataChange();
            }
        }

        public static void updateShoppingList(CallBehavior behavior){
            switch (behavior){
                case WAIT_FOR_RETURN: {
                    ApiResponse<ShoppingList> apiResponse = ServerCalls.getShoppingList();
                    if(apiResponse != null && apiResponse.getData() != null){
                        shoppingListItems.clear();
                        addAllShoppingListItems(apiResponse.getData());
                    }
                }
                case PARALLEL: {
                    ServerCalls.getShoppingListAsync(new ServerCalls.OnAsyncCallListener<ShoppingList>() {
                        @Override
                        public void onFailure(ApiException e) {
                            //TODO: Implement failure
                        }

                        @Override
                        public void onSuccess(ShoppingList result) {
                            shoppingListItems.clear();
                            addAllShoppingListItems(result);
                        }
                    });
                }
            }
        }

        public static void updateShoppingList(final ServerCalls.OnAsyncCallListener<ShoppingList> listener){
            if(listener != null){
                ServerCalls.getShoppingListAsync(new ServerCalls.OnAsyncCallListener<ShoppingList>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(ShoppingList result) {
                        shoppingListItems.clear();
                        listener.onSuccess(result);
                        addAllShoppingListItems(result);
                    }
                });
            }
        }

        public static void createShoppingListItem(CallBehavior behavior, ListItem item){
            if(item != null) {
                switch (behavior) {
                    case WAIT_FOR_RETURN: {
                        ApiResponse<ListItem> apiResponse = ServerCalls.createShoppingListItem(item);
                        if(apiResponse != null && apiResponse.getData() != null){
                            item = apiResponse.getData();
                            shoppingListItems.add(item);
                            callAllListeners();
                        }
                        break;
                    }
                    case PARALLEL: {
                        ServerCalls.createShoppingListItemAsync(item, new ServerCalls.OnAsyncCallListener<ListItem>() {
                            @Override
                            public void onFailure(ApiException e) {
                                //TODO: Implement failure
                            }

                            @Override
                            public void onSuccess(ListItem result) {
                                shoppingListItems.add(result);
                                callAllListeners();
                            }
                        });
                        break;
                    }
                }
            }
        }

        public static void createShoppingListItem(ListItem item, final ServerCalls.OnAsyncCallListener<ListItem> listener){
            if(item != null && listener != null){
                ServerCalls.createShoppingListItemAsync(item, new ServerCalls.OnAsyncCallListener<ListItem>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(ListItem result) {
                        shoppingListItems.add(result);
                        listener.onSuccess(result);
                        callAllListeners();
                    }
                });
            }
        }
    }

    /**
     * DataContainer for selected ShoppingList Items
     * <p>
     *     Contains all selected ShoppingList items
     * </p>
     */

    public static abstract class SelectedShoppingListItems {
        private static ArrayList<ListItem> selectedShoppingListItems;

        private static ArrayList<OnDataChangeListener> mListeners;

        private static void initialize(){
         selectedShoppingListItems = new ArrayList<>();
         mListeners = new ArrayList<>();
        }

        /**
         * Adds an instance of ListItem to SelectedShoppingListItems
         * <p/>
         * @param item An Instance of ListItem which will be added to the List of selected
         *             ShoppingList Items
         */

        public static void addSelectedShoppingListItem(ListItem item){
            selectedShoppingListItems.add(item);
            callAllListeners();
        }

        /**
         * Removes an Item from the selected ShoppingList items
         * <p/>
         * @param item An Instance of ListItem which will be removed from the selected ShoppingList
         *             items
         */

        public static void removeSelectedShoppingListItem(ListItem item){
            if(selectedShoppingListItems.remove(item)) {
                callAllListeners();
            }
        }

        /**
         * Method to buy all selected ShoppingList items.
         * <p/>
         * Removes all items contained in the SelectedShoppingListItem from the ShoppingList
         * and clears the SelectedShoppingListItems list. All selected items will be sent to the Server.
         * If it succeed the ShoppingList will be updated
         */

        public static void removeSelection(){
            ShoppingListItems.shoppingListItems.removeAll(selectedShoppingListItems);

            //TODO: Publish bought items on Server

            selectedShoppingListItems.clear();
            callAllListeners();
        }

        /**
         * Returns the amount of Items which are currently selected
         * <p/>
         * @return int
         */

        public static int getSelectedShoppingListItemsCount(){
            return selectedShoppingListItems.size();
        }

        /**
         * Method to add an OnDataChangeListener to the SelectedShoppingListItems
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 SelectedShoppingListItems Data changes
         */

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        /**
         * Method to remove an OnDataChangeListener from the SelectedShoppingListItems
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 SelectedShoppingListItems Data changes
         */

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

        /**
         * Method to remove all OnDataChangeListeners currently attached to the SelectedShoppingListItems
         */

        public static void removeAllOnDataChangeListeners(){
            mListeners.clear();
        }

        private static void callAllListeners(){
            for(OnDataChangeListener listener: mListeners){
                listener.onDataChange();
            }
        }
    }

    /**
     * DataContainer for User-Instances
     **/

    public static abstract class Users {
        private static ArrayList<User> users;

        private static ArrayList<OnDataChangeListener> mListeners;

        private static void initialize(){
            users = new ArrayList<>();
            mListeners = new ArrayList<>();
            GroupMembers.addOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    addAllUsers((ArrayList<User>) GroupMembers.members);
                }
            });
        }

        private static void addUser(User user){
            if(user != null && !users.contains(user)) {
                users.add(user);
                callAllListeners();
            }
        }

        /**
         * Adds a list of users to the current list of users
         * <p/>
         * @param users List of users which can be null
         */

        protected static void addAllUsers(ArrayList<User> users) {
            if (users != null) {
                Users.users.clear();
                Users.users.addAll(users);
                callAllListeners();
            }
        }

        /**
         * Returns the DisplayName of a user if the users list contains it
         * <p/>
         * @param uid The Uid of the user (Should not be null)
         * @return Either the DisplayName of the user or null
         */

        @Nullable
        public static String getDisplayNameByUid(String uid) {
            for (User user : users) {
                if (user.getUid().equals(uid)) {
                    return user.getDisplayName();
                }
            }

            return null;
        }

        /**
         * Returns concatenated String of DisplayNames
         * <p/>
         * @param concat A concatenated String of uids seperated by ' || '
         * @return Either the concatenated String of displayNames or an empty string
         */

        public static String getConcatDisplayNames(String concat){
            ArrayList<String> uids = getUidsFromConcat(concat);

            if(uids.size() == users.size()){
                return "Group";
            }

            String concatDisplayNames = "";

            for(int i = 0; i < uids.size(); i++){
                if(i == 0){
                    concatDisplayNames = getDisplayNameByUid(uids.get(i));
                } else {
                    concatDisplayNames = concatDisplayNames + ", " + getDisplayNameByUid(uids.get(i));
                }
            }

            return concatDisplayNames;
        }

        private static ArrayList<String> getUidsFromConcat(String concat){
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9]* || $");
            Matcher matcher = pattern.matcher(concat);

            ArrayList<String> uids = new ArrayList<>();

            if(matcher.matches()){
                for(int i = 0; i < matcher.groupCount(); i++){
                    String substring = matcher.group(i);
                    if(substring.endsWith(" || ")){
                        substring.replace(" || ", "");
                    }

                    uids.add(substring);
                }
            }

            return uids;
        }

        /**
         * Returns a list of all current users
         * <p/>
         * @return A list on users
         */

        public static ArrayList<User> getAll() {
            return (ArrayList<User>) users.clone(); //returns cloned List to avoid changes on Users.users
        }

        /**
         * Returns the user for a given index
         * <p/>
         * @param index The index of the user
         * @return Either a user Instance or null
         */

        @Nullable
        public static User getUser(int index) {
            if (index < users.size()) {
                return users.get(index);
            }

            return null;
        }

        /**
         * Removes a user from the current list of users
         * <p/>
         * @param user A user Instance which will be removed from the current users
         */

        protected static void removeUser(User user) {
            if(users.remove(user)) {
                callAllListeners();
            }
        }

        /**
         * Method to add an OnDataChangeListener to the users
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 users Data changes
         */

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        /**
         * Method to remove an OnDataChangeListener from the users
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 users Data changes
         */

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

        /**
         * Method to remove all OnDataChangeListeners currently attached to the users
         */

        public static void removeAllOnDataChangeListeners(){
            mListeners.clear();
        }

        private static void callAllListeners(){
            for(OnDataChangeListener listener: mListeners){
                listener.onDataChange();
            }
        }
    }

    /**
     * DataContainer for Me (current User)
     */

    public static abstract class Me {
        private static User me;

        private static ArrayList<OnDataChangeListener> mListeners;

        private static void initialize(){
            me = new User();
            mListeners = new ArrayList<>();
        }

        /**
         * Returns the user instance assigned to the phone owner
         * <p/>
         * @return An Instance of User
         */

        public static User getMe() {
            return me;
        }

        /**
         * Sets the user Instance assigned to the phone owner and stores his data locally
         * <p/>
         * @param me An Instance of user which will be assigned to the phone owner
         */

        protected static void setMe(User me) {
            if(me != null && !Me.me.equals(me)){
                storeMe(me);
                updateMe(CallBehavior.PARALLEL, me);

                callAllListeners();
            }
        }

        protected static void updateFirebaseInstanceIdToken(String token){
            me.setFirebaseInstanceId(token);
            Configuration.singleton.addConfig(Configuration.Type.FIREBASE_INSTANCE_ID, token);
            if(me.getUid() != null){
                updateMe(me, null);
            }
        }

        /**
         * Updates the uid of the user if it differs from the old one
         * <p/>
         * @param uid A String containing the uid of the user (can be null)
         */

        public static void updateUid(String uid){
            if(uid != null && !uid.isEmpty()){
                me.setUid(uid);
                updateMe(CallBehavior.PARALLEL, me);
                callAllListeners();
            }
        }

        /**
         * Updates the displayName of the user if it differs form the old one
         * <p/>
         * @param displayName A String containing the displayName of the user (can be null)
         */

        public static void updateDisplayName(String displayName){
            if(displayName != null && !displayName.isEmpty()){
                if(me.getDisplayName() == null ){
                    me.setDisplayName(displayName);
                    Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);
                    updateMe(CallBehavior.PARALLEL, me);
                    callAllListeners();
                } else if(!me.getDisplayName().equals(displayName)){
                    me.setDisplayName(displayName);
                    Configuration.singleton.addConfig(Configuration.Type.USER_DISPLAY_NAME, displayName);
                    updateMe(CallBehavior.PARALLEL, me);
                    callAllListeners();
                }
            }
        }

        /**
         * Updates the email address of the user
         * <p/>
         * @param email A string containing the email address of the user (can be null)
         */

        public static void updateEmailAddress(String email){
            me.setEmail(email);
            Configuration.singleton.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, email);
            updateMe(CallBehavior.PARALLEL, me);
            callAllListeners();
        }

        public static void updateMe(CallBehavior behavior, User user){
            switch (behavior){
                case WAIT_FOR_RETURN: {
                    ApiResponse<User> userResponse = ServerCalls.updateUser(user);
                    if(userResponse != null){
                        if(userResponse.getData() != null){
                            setMe(userResponse.getData());
                            callAllListeners();
                        }
                    }
                    break;
                }
                case PARALLEL: {
                    ServerCalls.updateUserAsync(user, new ServerCalls.OnAsyncCallListener<User>() {
                        @Override
                        public void onFailure(ApiException e) {
                            //TODO: Implement onFailure
                        }

                        @Override
                        public void onSuccess(User result) {
                            setMe(result);
                            callAllListeners();
                        }
                    });
                    break;
                }
            }
        }

        public static void updateMe(User user, final ServerCalls.OnAsyncCallListener<User> listener){
            if(user != null){
                ServerCalls.getUserAsync(me.getUid(), new ServerCalls.OnAsyncCallListener<User>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(User result) {
                        if(listener != null) {
                            listener.onSuccess(result);
                            callAllListeners();
                        }
                    }
                });
            }
        }

        public static void initializeMe(final ServerCalls.OnAsyncCallListener<User> listener){
            if(listener != null){
                ServerCalls.getUserAsync(me.getUid(), new ServerCalls.OnAsyncCallListener<User>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(User result) {
                        setMe(result);
                        listener.onSuccess(result);
                        callAllListeners();
                    }
                });
            }
        }

        public static void registerMe(final ServerCalls.OnAsyncCallListener<User> listener){
            if(me != null && listener != null){
                ServerCalls.createUserAsync(me, new ServerCalls.OnAsyncCallListener<User>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(User result) {
                        setMe(result);
                        listener.onSuccess(result);
                    }
                });
            }
        }

        private static void storeMe(User user){
            if(user != null){
                Me.me = user;

                Configuration config = Configuration.singleton;

                if(user.getUid() != null){
                    config.addConfig(Configuration.Type.USER_UID, user.getUid());
                }

                if(user.getDisplayName() != null){
                    config.addConfig(Configuration.Type.USER_DISPLAY_NAME, user.getDisplayName());
                }

                if(user.getEmail() != null){
                    config.addConfig(Configuration.Type.USER_EMAIL_ADDRESS, user.getEmail());
                }

                if(user.getGroupUid() != null){
                    config.addConfig(Configuration.Type.USER_GROUP_ID, user.getGroupUid().toString());
                }
            }
        }

        /**
         * Method to add an OnDataChangeListener to the current user
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 the data of the current user changes
         */

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        /**
         * Method to remove an OnDataChangeListener from the current user
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 the data of the current user changes
         */

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

        /**
         * Method to remove all OnDataChangeListeners currently attached to the current user
         */

        public static void removeAllOnDataChangeListeners(){
            mListeners.clear();
        }

        private static void callAllListeners(){
            for(OnDataChangeListener listener: mListeners){
                listener.onDataChange();
            }
        }
    }

    /**
     * DataContainer for the group
     */

    public static abstract class Groups {
        private static Group group;

        private static ArrayList<OnDataChangeListener> mListeners;

        private static void initialize(){
            group = new Group();
            mListeners = new ArrayList<>();
            Me.addOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    if(Me.me.getGroupUid() != null){
                        if(group.getUid() != null && !Me.me.getGroupUid().equals(group.getUid())){
                            getGroupFromServer(CallBehavior.PARALLEL);
                        } else if(group.getUid() == null){
                            getGroupFromServer(CallBehavior.PARALLEL);
                        }
                    }
                }
            });
        }

        /**
         * Returns the group assigned to the current user
         * <p/>
         * @return An Instance of Group
         */

        public static Group getGroup() {
            return group;
        }

        /**
         * Sets the group assigned to the current user
         * <p/>
         * @param group An Instance of Group (can be null)
         */

        public static void setGroup(Group group) {
            Groups.group = group;
            if(group != null) {
                Me.me.setGroupUid(group.getUid());
                Me.callAllListeners();
            }
            callAllListeners();
        }

        public static void getGroupFromServer(CallBehavior behavior){
            if(Me.me.getGroupUid() != null) {
                switch (behavior) {
                    case WAIT_FOR_RETURN: {
                        ApiResponse<Group> groupApiResponse = ServerCalls.getGroup(Me.me.getGroupUid().toString());
                        if (groupApiResponse != null && groupApiResponse.getData() != null) {
                            group = groupApiResponse.getData();
                            callAllListeners();
                        }
                        break;
                    }
                    case PARALLEL: {
                        ServerCalls.getGroupAsync(Me.me.getGroupUid().toString(), new ServerCalls.OnAsyncCallListener<Group>() {
                            @Override
                            public void onFailure(ApiException e) {
                                //TODO: Implement failure
                            }

                            @Override
                            public void onSuccess(Group result) {
                                group = result;
                                callAllListeners();
                            }
                        });
                        break;
                    }
                }
            }
        }

        public static void getGroupFromServer(final ServerCalls.OnAsyncCallListener<Group> listener){
            if(listener != null){
                ServerCalls.getGroupAsync(Me.me.getGroupUid().toString(), new ServerCalls.OnAsyncCallListener<Group>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(Group result) {
                        listener.onSuccess(result);
                        callAllListeners();
                    }
                });
            }
        }

        public static void createGroup(final Group group, final ServerCalls.OnAsyncCallListener<Group> listener){
            if(group != null && listener != null){
                ServerCalls.createGroupAsync(group, new ServerCalls.OnAsyncCallListener<Group>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(Group result) {
                        Groups.group = result;
                        Me.me.setGroupUid(result.getUid());
                        Me.updateMe(CallBehavior.PARALLEL, Me.me);
                        listener.onSuccess(result);
                    }
                });
            }
        }

        public static void joinGroup(String accessKey, final ServerCalls.OnAsyncCallListener<Group> listener){
            if(accessKey != null && listener != null){
                ServerCalls.joinGroupAsync(accessKey, new ServerCalls.OnAsyncCallListener<Group>() {
                    @Override
                    public void onFailure(ApiException e) {
                        listener.onFailure(e);
                    }

                    @Override
                    public void onSuccess(Group result) {
                        group = result;
                        Me.me.setGroupUid(result.getUid());
                        Me.updateMe(CallBehavior.PARALLEL, Me.me);
                        listener.onSuccess(result);
                        callAllListeners();
                    }
                });
            }
        }

        /**
         * Method to add an OnDataChangeListener to the current group
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 the data of the current group changes
         */

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        /**
         * Method to remove an OnDataChangeListener from the current group
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 the data of the current group changes
         */

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

        /**
         * Method to remove all OnDataChangeListeners currently attached to the current group
         */

        public static void removeAllOnDataChangeListeners(){
            mListeners.clear();
        }

        private static void callAllListeners(){
            for(OnDataChangeListener listener: mListeners){
                listener.onDataChange();
            }
        }
    }

    /**
     * DataContainer for group members
     */

    public static abstract class GroupMembers {
        private static List<User> members;

        private static ArrayList<OnDataChangeListener> mListeners;

        private static void initialize(){
            members = new ArrayList<>();
            mListeners = new ArrayList<>();
            Groups.addOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDataChange() {
                    initializeGroupMembers(CallBehavior.PARALLEL);
                }
            });
        }

        /**
         * Sets the list of group members of the group which is currently attached to the phone owner
         * <p/>
         * @param members A list of users (Should not be null)
         */

        public static void setMembers(List<User> members) {
            GroupMembers.members.clear();
            GroupMembers.members.addAll(members);

            if(members != null){
                for(User user: members){
                    Users.addUser(user);
                }
            }

            callAllListeners();
        }

        /**
         * Returns a list of users which are members of the current group
         * <p/>
         * @return list of user (can not be null)
         */

        public static List<User> getMembers() {
            if (members != null) {
                return members;
            }

            return new ArrayList<>();
        }

        public static void initializeGroupMembers(CallBehavior behavior){
            if(Groups.group.getMembers() != null){
                List<String> members = Groups.group.getMembers();
                GroupMembers.members.clear();

                for(String uid: members){
                    ApiResponse<User> userResponse = ServerCalls.getUser(uid);
                    if(userResponse != null && userResponse.getData() != null){
                        GroupMembers.members.add(userResponse.getData());
                    }
                }

                callAllListeners();
            }
        }

        /**
         * Method to add an OnDataChangeListener to the list of group members
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 the list of group members changes
         */

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        /**
         * Method to remove an OnDataChangeListener from list of group members
         *
         * @param listener Instance of OnDataChangeListener which will be called when
         *                 the list of group members changes
         */

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

        /**
         * Method to remove all OnDataChangeListeners currently attached to the list of group members
         */

        public static void removeAllOnDataChangeListeners(){
            mListeners.clear();
        }

        private static void callAllListeners(){
            for(OnDataChangeListener listener: mListeners){
                listener.onDataChange();
            }
        }
    }
}
