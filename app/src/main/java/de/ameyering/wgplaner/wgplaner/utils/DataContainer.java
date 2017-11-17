package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.client.model.*;
import io.swagger.client.model.User;

public abstract class DataContainer {

    public interface OnDataChangeListener {
        void onDataChange();
    }

    /**
     * DataContainer for ShoppingListItems
     */

    public static abstract class ShoppingListItems {
        private static ArrayList<ListItem> shoppingListItems = new ArrayList<>();

        private static ArrayList<OnDataChangeListener> mListeners = new ArrayList<>();

        public static ArrayList<ListItem> getShoppingListItems() {
            ArrayList<ListItem> items = new ArrayList<>();
            items.addAll(shoppingListItems);
            return items;
        }

        public static void addShoppingListItem(String name, int number, List<String> requestedFor) {
            ListItem item = new ListItem();
            item.setTitle(name);
            item.setRequestedFor(requestedFor);
            item.setRequestedBy(DataContainer.Me.getMe().getUid());

            int pos = contains(item);

            if (pos == -1) {
                shoppingListItems.add(item);
                callAllListeners();

            } else {
                //TODO: Increment number
            }
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

        public static void removeShoppingListItem(ListItem item) {
            int pos = contains(item);

            if (pos != -1) {
                shoppingListItems.remove(pos);
                callAllListeners();
            }
        }

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

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
     * DataContainer for selected ShoppingList Items
     */

    public static abstract class SelectedShoppingListItems {
        private static ArrayList<ListItem> selectedShoppingListItems = new ArrayList<>();

        private static ArrayList<OnDataChangeListener> mListeners = new ArrayList<>();


        public static void addSelectedShoppingListItem(ListItem item){
            selectedShoppingListItems.add(item);
            callAllListeners();
        }

        public static void removeSelectedShoppingListItem(ListItem item){
            if(selectedShoppingListItems.remove(item)) {
                callAllListeners();
            }
        }

        public static void removeSelection(){
            for(ListItem item: selectedShoppingListItems){
                ShoppingListItems.shoppingListItems.remove(item);
            }

            selectedShoppingListItems.clear();
            callAllListeners();
        }

        public static int getSelectedShoppingListItemsCount(){
            return selectedShoppingListItems.size();
        }

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

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
        private static ArrayList<User> users = new ArrayList<>();

        private static ArrayList<OnDataChangeListener> mListeners = new ArrayList<>();

        private static void addUser(User user){
            if(user != null) {
                users.add(user);
                callAllListeners();
            }
        }

        protected static void addAllUsers(ArrayList<User> users) {
            if (users != null) {
                Users.users.addAll(users);
                callAllListeners();
            }
        }

        @Nullable
        public static String getDisplayNameByUid(String uid) {
            for (User user : users) {
                if (user.getUid().equals(uid)) {
                    return user.getDisplayName();
                }
            }

            return null;
        }

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

        public static ArrayList<User> getAll() {
            return (ArrayList<User>) users.clone(); //returns cloned List to avoid changes on Users.users
        }

        @Nullable
        public static User getUser(int index) {
            if (index < users.size()) {
                return users.get(index);
            }

            return null;
        }

        protected static void removeUser(User user) {
            if(users.remove(user)) {
                callAllListeners();
            }
        }

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

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

        private static ArrayList<OnDataChangeListener> mListeners = new ArrayList<>();

        public static User getMe() {
            return me;
        }

        public static void setMe(User me) {
            Me.me = me;

            if(!Users.users.contains(me)){
                Users.addUser(me);
                callAllListeners();
            }
        }

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

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

        private static ArrayList<OnDataChangeListener> mListeners = new ArrayList<>();

        public static Group getGroup() {
            return group;
        }

        public static void setGroup(Group group) {
            Groups.group = group;
            callAllListeners();
        }

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

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
        private static List<User> members = new ArrayList<>();

        private static ArrayList<OnDataChangeListener> mListeners = new ArrayList<>();

        public static void setMembers(List<User> members) {
            GroupMembers.members.clear();
            GroupMembers.members.addAll(members);
            callAllListeners();
        }

        public static List<User> getMembers() {
            if (members != null) {
                return members;
            }

            return new ArrayList<>();
        }

        public static void addOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.add(listener);
            }
        }

        public static void removeOnDataChangeListener(OnDataChangeListener listener){
            if(listener != null){
                mListeners.remove(listener);
            }
        }

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
