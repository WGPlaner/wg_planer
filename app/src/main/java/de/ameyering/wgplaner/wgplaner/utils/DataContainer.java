package de.ameyering.wgplaner.wgplaner.utils;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.ameyering.wgplaner.wgplaner.structure.Item;
import de.ameyering.wgplaner.wgplaner.structure.User;
import io.swagger.client.model.ListItem;

public abstract class DataContainer {

    public static abstract class ListItems {
        private static ArrayList<ListItem> shoppingListItems = new ArrayList<>();

        public static ArrayList<ListItem> getShoppingListItems(){
            ArrayList<ListItem> items = new ArrayList<>();
            items.addAll(shoppingListItems);
            return items;
        }

        public static void addShoppingListItem(String name, int number, List<String> requestedFor, String requestedBy){
            ListItem item = new ListItem();
            item.setTitle(name);
            item.setRequestedFor(requestedFor);
            item.setRequestedBy(requestedBy);

            int pos = contains(item);

            if(pos == -1){
                shoppingListItems.add(item);
            } else {
                //TODO: Increment number
            }
        }

        private static int contains(ListItem item){
            for(int i = 0; i < shoppingListItems.size(); i++){
                ListItem listItem = shoppingListItems.get(i);
                if(
                    listItem.getTitle() != null && item.getTitle() != null &&
                    listItem.getRequestedFor() != null && item.getRequestedFor() != null){
                    if(listItem.getTitle().equals(item.getTitle()) && listItem.getRequestedFor().equals(item.getRequestedFor())){
                        return i;
                    }
                }
            }

            return -1;
        }

        public static void removeShoppingListItem(ListItem item){
            int pos = contains(item);

            if(pos != -1){
                shoppingListItems.remove(pos);
            }
        }
    }

    /**
     * DataContainer for Item-Instances
     **/

    public static abstract class Items {
        private static ArrayList<Item> items = new ArrayList<>();
        private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        public static ArrayList<Item> getItems() {
            return (ArrayList<Item>) items.clone();
        }

        public static void addItem(Item item) {
            items.add(item);
        }

        @Nullable
        public static Item getItem(int index) {
            if (index < items.size()) {
                return items.get(index);
            }

            return null;
        }

        public static void removeItem(Item item) {
            items.remove(item);
        }

        public static int getSize() {
            return items.size();
        }

        public static SimpleDateFormat getDateFormat() {
            return dateFormat;
        }
    }

    /**
     * DataContainer for User-Instances
     **/

    public static abstract class Users {
        private static ArrayList<User> users = new ArrayList<>();

        protected static void addAllUsers(ArrayList<User> users) {
            if (users != null) {
                Users.users.addAll(users);
            }
        }

        @Nullable
        public static String getDisplayNameByUid(String uid) {
            User user = new User(uid, "");

            if (users.contains(user)) {
                int index = users.indexOf(user);
                return users.get(index).getDisplayName();
            }

            return null;
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
            users.remove(user);
        }
    }
}
